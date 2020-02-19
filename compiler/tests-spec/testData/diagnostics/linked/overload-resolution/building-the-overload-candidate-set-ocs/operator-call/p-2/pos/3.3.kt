// !LANGUAGE: +NewInference
// !DIAGNOSTICS: -UNUSED_VARIABLE -ASSIGNED_BUT_NEVER_ACCESSED_VARIABLE -UNUSED_VALUE -UNUSED_PARAMETER -UNUSED_EXPRESSION
// SKIP_TXT

/*
 * KOTLIN DIAGNOSTICS SPEC TEST (POSITIVE)
 *
 * SPEC VERSION: 0.1-268
 * PLACE: overload-resolution, building-the-overload-candidate-set-ocs, operator-call -> paragraph 2 -> sentence 3
 * RELEVANT PLACES: overload-resolution, building-the-overload-candidate-set-ocs, call-with-an-explicit-receiver -> paragraph 6 -> sentence 3
 * overload-resolution, building-the-overload-candidate-set-ocs, operator-call -> paragraph 4 -> sentence 1
 * NUMBER: 3
 * DESCRIPTION: Explicitly imported extension callables
 */


// FILE: LibCase1.kt
// TESTCASE NUMBER: 1
package libPackage1
import testPackCase1.Case
import testPackCase1.Case.Inv
import testPackCase1.Case.E

operator fun Case.E.plus(value: Int) =  Inv()
operator fun Case.Inv.invoke(i: Int) = 1

// FILE: LibCase1.kt
// TESTCASE NUMBER: 1
package libPackage2
import testPackCase1.Case
import testPackCase1.Case.Inv
import testPackCase1.Case.E

operator fun Case.E.plus(value: Int) =  Inv()
operator fun Case.Inv.invoke(i: Int) = 1


// FILE: TestCase1.kt
// TESTCASE NUMBER: 1
package testPackCase1
import libPackage1.plus
import libPackage1.invoke
import libPackage2.*
class Case() {

    class E(val plus: Inv? = null) {
        /*operator*/ fun plus(value: Int) = Case()
    }

    class Inv() {
        /*operator*/ fun invoke(value: Int) = Case()
    }

    fun foo(e: E) {
        /*operator*/ fun E.<!EXTENSION_SHADOWED_BY_MEMBER!>plus<!>(value: Int) = Case()

        run {
            <!DEBUG_INFO_AS_CALL("fqName: libPackage1.plus; typeCall: operator extension function")!>e + 1<!>
        }
        <!DEBUG_INFO_AS_CALL("fqName: libPackage1.plus; typeCall: operator extension function")!>e + 1<!>

    }
}

// FILE: LibCase1.kt
// TESTCASE NUMBER: 2
package libPackage1
import testPackCase2.Case
import testPackCase2.Case.Inv
import testPackCase2.Case.E

operator fun Case.E.plus(value: Int) =  Inv()
operator fun Case.Inv.invoke(i: Int) = 1

// FILE: LibCase1.kt
// TESTCASE NUMBER: 2
package libPackage2
import testPackCase2.Case
import testPackCase2.Case.Inv
import testPackCase2.Case.E

operator fun Case.E.plus(value: Int) =  Inv()
operator fun Case.Inv.invoke(i: Int) = 1


// FILE: TestCase1.kt
// TESTCASE NUMBER: 2
package testPackCase2
import libPackage1.plus
import libPackage1.invoke
import libPackage2.*
class Case() {

    class E(val plus: Inv? = null) {
        /*operator*/ fun plus(value: Int) = Case()
    }

    class Inv() {
        /*operator*/ fun invoke(value: Int) = Case()
    }

    fun foo(e: E) {
        /*operator*/ fun E.<!EXTENSION_SHADOWED_BY_MEMBER!>plus<!>(value: Int) = Case()

        run {
            /*operator*/ fun E.<!EXTENSION_SHADOWED_BY_MEMBER!>plus<!>(value: Int) = Case()

            <!DEBUG_INFO_AS_CALL("fqName: libPackage1.plus; typeCall: operator extension function")!>e + 1<!>
        }
        <!DEBUG_INFO_AS_CALL("fqName: libPackage1.plus; typeCall: operator extension function")!>e + 1<!>
    }
}