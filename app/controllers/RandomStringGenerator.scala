package uk.co.tkeetch.sso.util

import java.security.SecureRandom

object RandomStringGenerator {

  private def prng = new SecureRandom()

  def getString(byteLen:Integer):String = {
    val bytes:Integer = if (byteLen <= 0) 20 else byteLen
    val randomBytes = new Array[Byte](bytes)
    val unit = prng.nextBytes(randomBytes)
    randomBytes.map("%02X".format(_)).mkString
  }

}

