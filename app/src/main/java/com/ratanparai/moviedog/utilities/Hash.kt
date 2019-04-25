package com.ratanparai.moviedog.utilities

import java.math.BigInteger
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


fun MD5(s: String): String {
    var m: MessageDigest? = null

    try {
        m = MessageDigest.getInstance("MD5")
    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    }


    m!!.update(s.toByteArray(), 0, s.length)
    return BigInteger(1, m!!.digest()).toString(16)
}