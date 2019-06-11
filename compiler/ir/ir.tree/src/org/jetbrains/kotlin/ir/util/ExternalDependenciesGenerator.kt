/*
 * Copyright 2010-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.ir.util

import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.descriptors.ModuleDescriptor
import org.jetbrains.kotlin.ir.declarations.IrDeclaration
import org.jetbrains.kotlin.ir.declarations.IrDeclarationOrigin
import org.jetbrains.kotlin.ir.descriptors.IrBuiltIns
import org.jetbrains.kotlin.ir.symbols.IrSymbol
import kotlin.math.min

class ExternalDependenciesGenerator(
    moduleDescriptor: ModuleDescriptor,
    val symbolTable: SymbolTable,
    val irBuiltIns: IrBuiltIns,
    externalDeclarationOrigin: ((DeclarationDescriptor) -> IrDeclarationOrigin)? = null,
    private val deserializer: IrDeserializer = EmptyDeserializer
) {
    private val stubGenerator = DeclarationStubGenerator(
        moduleDescriptor, symbolTable, irBuiltIns.languageVersionSettings, externalDeclarationOrigin, deserializer
    )

    fun generateUnboundSymbolsAsDependencies() {
        // We need at least one iteration; deserialization may lead to new unbound symbols being produced.
        var needMoreIterations = true
        while (needMoreIterations) {
            needMoreIterations = false

            stubGenerator.unboundSymbolGeneration = true
            val allUnbound = symbolTable.run {
                unboundClasses + unboundConstructors + unboundEnumEntries + unboundFields +
                        unboundSimpleFunctions + unboundProperties + unboundTypeParameters
            }
            for (symbol in allUnbound) {
                deserializer.findDeserializedDeclaration(symbol, stubGenerator::generateStubBySymbol)
                if (symbol.isBound) needMoreIterations = true
            }
        }

        deserializer.declareForwardDeclarations()

        assertEmpty(symbolTable.unboundClasses, "classes")
        assertEmpty(symbolTable.unboundConstructors, "constructors")
        assertEmpty(symbolTable.unboundEnumEntries, "enum entries")
        assertEmpty(symbolTable.unboundFields, "fields")
        assertEmpty(symbolTable.unboundSimpleFunctions, "simple functions")
        assertEmpty(symbolTable.unboundProperties, "properties")
        assertEmpty(symbolTable.unboundTypeParameters, "type parameters")
    }

    private fun assertEmpty(s: Set<IrSymbol>, marker: String) {
        assert(s.isEmpty()) {
            "$marker: ${s.size} unbound:\n" +
                    s.toList().subList(0, min(10, s.size)).joinToString(separator = "\n") { it.descriptor.toString() }
        }
    }
}

object EmptyDeserializer : IrDeserializer {
    override fun findDeserializedDeclaration(symbol: IrSymbol, backoff: (IrSymbol) -> IrDeclaration): IrDeclaration? = backoff(symbol)

    override fun declareForwardDeclarations() {}
}