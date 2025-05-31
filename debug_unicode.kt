fun main() {
    val text = "Hello 🌟 world"
    println("Text: $text")
    println("Length: ${text.length}")
    println("Code points: ${text.codePointCount(0, text.length)}")
    
    // Test other strings from failed tests
    val text2 = "Hello world\ntest"
    println("\nText2: $text2")
    println("Length2: ${text2.length}")
    
    val text3 = "Auto save and load test"
    println("\nText3: $text3")
    println("Length3: ${text3.length}")
    println("Word count3: ${text3.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }.size}")
}