/*
The MIT License (MIT)

Copyright (c) 2022 Augustus

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
import com.github.labai.utils.mapper.LaCopyable
import com.github.labai.utils.mapper.assignFields
import com.github.labai.utils.mapper.laCopy
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

/*
 * @author Augustus
 * created on 2025-11-24
*/
class LaCopyableTest {

    class Sample(val a1: String) : LaCopyable<Sample> {
        var a2: String? = "abc"
    }

    open class DtoPar1 {
        var p1: String? = null
    }

    interface IDtoPar2 {
        var p2: String?
    }

    class Dto(val a1: String, var a2: String) : DtoPar1(), IDtoPar2, LaCopyable<Dto> {
        var a3: String? = null
        override var p2: String? = "x"
    }

    @Test
    fun test_sample() {
        val sample = Sample("A1").apply {
            a2 = "A2"
        }
        val copy: Sample = sample.laCopy()
        assertEquals(sample.a1, copy.a1)
        assertEquals(sample.a2, copy.a2)
    }

    @Test
    fun test_lacopy_whenWithParent_copyAllFields() {
        val dto = Dto("a1-fr", "a2-fr").apply {
            a3 = "a3-fr"
            p1 = "p1-fr"
            p2 = "p2-fr"
        }
        val res: Dto = dto.laCopy()

        assertEquals(dto.a1, res.a1)
        assertEquals(dto.a2, res.a2)
        assertEquals(dto.a3, res.a3)
        assertEquals(dto.p1, res.p1)
        assertEquals(dto.p2, res.p2)
    }

    @Test
    fun test_lacopy_copyWithMapping() {
        val dto = Dto("a1-fr", "a2-fr").apply {
            a3 = "a3-fr"
            p1 = "p1-fr"
            p2 = "p2-fr"
        }
        val res: Dto = dto.laCopy {
            t::a1 from f::a2
            t::a2 from { "abra" }
            t::p1 from f::p2
        }

        assertEquals(dto.a2, res.a1)
        assertEquals("abra", res.a2)
        assertEquals(dto.a3, res.a3)
        assertEquals(dto.p2, res.p1)
        assertEquals(dto.p2, res.p2)
    }

    @Test
    fun test_assignFields() {
        val main = Dto("a1-to", "a2-to").apply {
            a3 = "a3-to"
            p1 = "p1-to"
            p2 = "p2-to"
        }
        val from1 = Dto("a1-fr1", "a2-fr1").apply {
            a3 = "a3-fr1"
            p1 = "p1-fr1"
            p2 = "p2-fr1"
        }

        val sample2 = Sample("a1-fr2").apply {
            a2 = "a2-fr2"
        }

        main.assignFields(from1) {
            t::p1 from f::p2
        }
        main.assignFields(sample2)

        assertEquals("a1-to", main.a1) // immutable
        assertEquals("a2-fr2", main.a2)
        assertEquals("a3-fr1", main.a3)
        assertEquals("p2-fr1", main.p1)
        assertEquals("p2-fr1", main.p2)
    }
}
