package com.whx.practice.test

/**
 * Created by whx on 2018/1/11.
 */

fun main(args: Array<String>) {
    println(Double.MAX_VALUE.toString())

    val test = mutableListOf(1, 2, 3)

    test.forEachIndexed { index, i ->
        println(index)
        println(i)
    }
}