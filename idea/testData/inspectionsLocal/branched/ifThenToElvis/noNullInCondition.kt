// PROBLEM: none
fun main(args: Array<String>) {
    val foo = "foo"
    val bar = "bar"
    if (foo == bar<caret>) {
        foo
    }
    else {
        bar
    }
}
