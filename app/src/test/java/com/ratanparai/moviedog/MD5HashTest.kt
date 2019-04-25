package com.ratanparai.moviedog

import com.google.common.truth.Truth.assertThat
import com.ratanparai.moviedog.utilities.MD5
import org.junit.Test

class MD5HashTest {

    @Test
    fun shouldGenerateHd5Hash() {
        var message = "Hello World!"

        val md5Hex = MD5(message).toUpperCase()

        assertThat(md5Hex).isEqualTo("ED076287532E86365E841E92BFC50D8C")
    }
}