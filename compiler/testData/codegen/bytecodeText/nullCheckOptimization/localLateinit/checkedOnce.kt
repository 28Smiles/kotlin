// IGNORE_BACKEND: JVM_IR
// TODO KT-36813 Support code generated by JVM_IR in redundant null check optimization
fun almostAlwaysTrue() = true

fun test() {
    lateinit var z: String
    run {
        if (almostAlwaysTrue()) {
            z = ""
        }
    }
    println(z)
    println(z)
    println(z)
}

// 0 IFNULL
// 1 IFNONNULL
// 1 throwUninitializedPropertyAccessException
