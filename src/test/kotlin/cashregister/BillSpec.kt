package cashregister

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertEquals


class BillSpec : Spek({
    describe("A Bill") {
        it("should set the dollarAmount and quantity values") {
            val bill = Bill(20, 2)
            assertEquals(20, bill.dollarAmount)
            assertEquals(2, bill.quantity)
        }

        it("should default quantity to 0 if not provided") {
            val bill = Bill(20)
            assertEquals(20, bill.dollarAmount)
            assertEquals(0, bill.quantity)
        }

        it("should calculate the total") {
            val bill = Bill(20, 2)
            assertEquals(40, bill.calculateTotal())
        }
    }
})

