package cashregister

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.context
import org.jetbrains.spek.api.dsl.describe
import org.jetbrains.spek.api.dsl.it
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class RegisterSpec : Spek({
   describe("a CashRegister") {
       context("with $20, $10, $5, $2, $1 bills") {
           var register = Register(arrayListOf())
           beforeEach {
               val bills: List<Bill> = listOf(
                       Bill(20),
                       Bill(10),
                       Bill(5),
                       Bill(2),
                       Bill(1)
               )
               register = Register(bills)
           }

           it("should send back the quantities of each bill in a string") {
               assertEquals("0 0 0 0 0", register.showQuantities())
           }

           it("should be able to add quantities to the given list of bills") {
               val billQuantities: List<Int> = arrayListOf(1, 0, 3, 4, 0)
               register.addQuantities(billQuantities)
               assertEquals("1 0 3 4 0", register.showQuantities())
           }

           it("should remove quantities from the given list of bills") {
               val billQuantities: List<Int> = arrayListOf(1, 0, 3, 4, 0)
               register.addQuantities(billQuantities)
               assertEquals("1 0 3 4 0", register.showQuantities())
               assertEquals(1, register.bills[0].quantity)
               assertEquals(0, register.bills[1].quantity)
               assertEquals(3, register.bills[2].quantity)
               assertEquals(4, register.bills[3].quantity)
               assertEquals(0, register.bills[4].quantity)
               register.removeQuantities(billQuantities)
               assertEquals("0 0 0 0 0", register.showQuantities())
               assertEquals(0, register.bills[0].quantity)
               assertEquals(0, register.bills[1].quantity)
               assertEquals(0, register.bills[2].quantity)
               assertEquals(0, register.bills[3].quantity)
               assertEquals(0, register.bills[4].quantity)
           }

           it("should fail if it cannot remove quantities") {
               val billQuantities: List<Int> = arrayListOf(1, 0, 2, 2, 0)
               assertFailsWith<Exception>("Not enough $20 bills to remove") { register.removeQuantities(billQuantities)}
           }

           it("should fail if it cannot remove quantities later in the list") {
               val billQuantities: List<Int> = arrayListOf(0, 0, 2, 2, 0)
               assertFailsWith<Exception>("Not enough $5 bills to remove") { register.removeQuantities(billQuantities)}
           }

           it("should fail if it the given list is not the same length as the amount of bills") {
               val billQuantities: List<Int> = arrayListOf(0, 0, 2, 2)
               assertFailsWith<Exception>("Invalid set of quantities to add.") { register.removeQuantities(billQuantities)}
           }

           it("should get the total amount of money in the register") {
               val billQuantities: List<Int> = arrayListOf(1, 0, 3, 4, 0)
               register.addQuantities(billQuantities)
               assertEquals(43, register.getTotal())
           }

           it("should get the correct request change") {
               val billQuantities: List<Int> = arrayListOf(1, 0, 3, 4, 0)
               register.addQuantities(billQuantities)
               val changeQuantities: List<Int> = register.sendChange(11)
               assertEquals(0, changeQuantities[0])
               assertEquals(0, changeQuantities[1])
               assertEquals(1, changeQuantities[2])
               assertEquals(3, changeQuantities[3])
               assertEquals(0, changeQuantities[4])
           }

           it("should throw an exception if change for the amount is not available") {
               val billQuantities: List<Int> = arrayListOf(1, 0, 3, 4, 0)
               register.addQuantities(billQuantities)
               register.sendChange(11)
               assertFailsWith<Exception>("Sorry") { register.sendChange(14) }
           }

           it("should throw an exception if you request for more change than is in the register") {
               assertFailsWith<Exception>("You can't request more than what is in the register") { register.sendChange(1) }
           }
       }
   }
})

