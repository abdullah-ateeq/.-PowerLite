package com.example.powerlite.domain.math

import java.math.BigInteger

class IntegerRootCalculator {

    fun integerNthRoot(value: BigInteger, index: Int): BigInteger {
        require(value >= BigInteger.ZERO) { "Value must be non-negative for integer nth root." }
        require(index >= 1) { "Root index must be at least 1." }

        if (value == BigInteger.ZERO) return BigInteger.ZERO
        if (value == BigInteger.ONE || index == 1) return value

        var ans = BigInteger.ONE
        var low = BigInteger.ONE
        var high = BigInteger.ONE.shiftLeft((value.bitLength() / index) + 1)

        while (low <= high) {
            val mid = low.add(high).shiftRight(1)
            val midPow = mid.pow(index)
            val cmp = midPow.compareTo(value)
            if (cmp == 0) {
                return mid
            } else if (cmp < 0) {
                ans = mid
                low = mid.add(BigInteger.ONE)
            } else {
                high = mid.subtract(BigInteger.ONE)
            }
        }
        return ans
    }
}
