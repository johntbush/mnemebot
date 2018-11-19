package org.mnemebot

import org.junit.Test

class StorageTest {
  @Test
  def test():Unit = {
    Storage.reset()
    assert(Storage.data("monica").size == 1)
    println(Storage.data)

    Storage.add("monica","blah")
    println(Storage.data)
    assert(Storage.data("monica").size == 2)

    Storage.remove("monica")
    assert(Storage.data.get("monica").isEmpty)

  }

}
