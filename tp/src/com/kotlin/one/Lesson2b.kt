package com.android.one

fun <T> runtest(name: String, expected: T, actual: T) {
    if (expected == actual) {
        println("✅ $name")
    } else {
        println("❌ $name -> expected=$expected actual=$actual")
    }
}

fun ex1CreateImmutableList(): List<Int> {
    return listOf(1, 2, 3, 4, 5)
}

fun ex2CreateMutableList(): MutableList<String> {
    val values = mutableListOf("alpha", "beta", "gamma")
    values.add("delta")
    return values
}

fun ex3FilterEvenNumbers(): List<Int> {
    return (1..10).filter { it % 2 == 0 }
}

fun ex4FilterAndMapAges(): List<String> {
    val ages = listOf(12, 18, 25, 16, 30)
    return ages.filter { it >= 18 }.map { "Adult: $it" }
}

fun ex5FlattenList(): List<Int> {
    val nested = listOf(listOf(1, 2), listOf(3, 4), listOf(5))
    return nested.flatten()
}

fun ex6FlatMapWords(): List<String> {
    val phrases = listOf("Kotlin is fun", "I love lists")
    return phrases.flatMap { it.split(" ") }
}

fun ex7EagerProcessing(): Pair<List<Long>, Long> {
    val start = System.currentTimeMillis()
    val result = (1..1_000_000)
        .filter { it % 3 == 0 }
        .map { it.toLong() * it }
        .take(5)
    val end = System.currentTimeMillis()
    return result to (end - start)
}

fun ex8LazyProcessing(): Pair<List<Long>, Long> {
    val start = System.currentTimeMillis()
    val result = (1..1_000_000)
        .asSequence()
        .filter { it % 3 == 0 }
        .map { it.toLong() * it }
        .take(5)
        .toList()
    val end = System.currentTimeMillis()
    return result to (end - start)
}

fun ex9FilterAndSortNames(): List<String> {
    val names = listOf("Alice", "bob", "Alex", "Charlie", "anna", "Aaron")
    return names.filter { it.startsWith('A') }.map { it.uppercase() }.sorted()
}

fun main() {
    runtest("ex1", listOf(1, 2, 3, 4, 5), ex1CreateImmutableList())
    runtest("ex2", mutableListOf("alpha", "beta", "gamma", "delta"), ex2CreateMutableList())
    runtest("ex3", listOf(2, 4, 6, 8, 10), ex3FilterEvenNumbers())
    runtest("ex4", listOf("Adult: 18", "Adult: 25", "Adult: 30"), ex4FilterAndMapAges())
    runtest("ex5", listOf(1, 2, 3, 4, 5), ex5FlattenList())
    runtest("ex6", listOf("Kotlin", "is", "fun", "I", "love", "lists"), ex6FlatMapWords())

    val eager = ex7EagerProcessing()
    runtest("ex7", listOf(9L, 36L, 81L, 144L, 225L), eager.first)
    println("ex7 time: ${eager.second} ms")

    val lazy = ex8LazyProcessing()
    runtest("ex8", listOf(9L, 36L, 81L, 144L, 225L), lazy.first)
    println("ex8 time: ${lazy.second} ms")

    runtest("ex9", listOf("AARON", "ALEX", "ALICE"), ex9FilterAndSortNames())
}
