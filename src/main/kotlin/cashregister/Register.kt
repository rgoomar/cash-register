package cashregister

import mu.KLogging

/**
 * Register class
 * @param bills A List of Bill objects
 */
class Register(val bills: List<Bill>){
    companion object: KLogging()

    /**
     * Sends back the quantities for each bill separated by a space
     * Example - "0 0 0 0 0"
     */
    fun showQuantities(): String {
        return bills.map(Bill::quantity).joinToString(" ")
    }

    /**
     * Iterates through each bill and adds up the total
     */
    fun getTotal(): Int {
        return bills.fold(0, { total, bill: Bill ->
            total.plus(bill.calculateTotal())
        })
    }

    /**
     * Adds a specific quantity for each bill
     *
     * Example:
     * List<Bill> = arrayListOf(Bill(20), Bill(10), Bill(5))
     * register.addQuantities(arrayListOf(1, 0, 1))
     * There will be one $20 bill and one $5 bill added
     *
     * @param quantities - List of quantities, must be in the same order as the list of bills
     */
    fun addQuantities(quantities: List<Int>) {
        if (quantities.size != bills.size) {
            throw Exception("Invalid set of quantities to add.")
        }
        for ((index, value) in quantities.withIndex()) {
            val bill: Bill = bills[index]
            bill.add(value)
        }
    }

    /**
     * Removes a specific quantity for each bill
     *
     * Example:
     * List<Bill> = arrayListOf(Bill(20), Bill(10), Bill(5))
     * register.addQuantities(arrayListOf(1, 0, 1))
     * There will be one $20 bill and one $5 bill removed
     *
     * @param quantities - List of quantities, must be in the same order as the list of bills
     */
    fun removeQuantities(quantities: List<Int>) {
        if (quantities.size != bills.size) {
            throw Exception("Invalid set of quantities to remove.")
        }
        for ((index, value) in quantities.withIndex()) {
            val bill: Bill = bills[index]
            if (bill.quantity < value) {
                throw Exception("Not enough \$${bill.dollarAmount} bills to remove")
            }
            bill.remove(value)
        }
    }

    /**
     * Find best combination of bill quantities for giving change back to the user
     * Removes the best combination before returning the list back
     *
     * @param requestedChange - Amount of change requested as an Int
     * @return List of quantities of bills for the change combination in the same order as the bills
     */
    fun sendChange(requestedChange: Int): List<Int> {
        if (requestedChange > getTotal()) {
            throw Exception("You can't request more than what is in the register")
        }

        var remainingTotal: Int = requestedChange
        val billsToRemove: MutableList<Int> = arrayListOf()
        kotlin.repeat(bills.size, { num -> billsToRemove.add(num, 0) })

        var biggestBillIndex = -1
        var counter = 0

        // Start finding the best combination
        while (counter < bills.size) {
            val bill = bills[counter]
            if (biggestBillIndex == counter) {
                var quantityOfBill = billsToRemove[counter]
                if (quantityOfBill == 0) {
                    biggestBillIndex = -1
                } else {
                    quantityOfBill = quantityOfBill.minus(1)
                    // Add that value back to restore the remainingTotal before moving onto the next combination set
                    remainingTotal = remainingTotal.plus(bill.dollarAmount.times(1))
                }
                billsToRemove[counter] = quantityOfBill
            } else {
                var amountOfBills = remainingTotal.div(bill.dollarAmount)
                // You can't remove what isn't there
                if (amountOfBills > bill.quantity) {
                    amountOfBills = 0
                }
                if (amountOfBills > 0) {
                    if (biggestBillIndex == -1) {
                        biggestBillIndex = counter
                    }
                    remainingTotal = remainingTotal.minus(bill.dollarAmount.times(amountOfBills))
                }
                billsToRemove[counter] = amountOfBills
            }
            if (counter == bills.size.minus(1)) {
                // If the biggestBillIndex is still set to -1, that means we have reached the last bill combination set
                if (remainingTotal != 0 && biggestBillIndex != -1 && biggestBillIndex != counter) {
                    // Restart the combination iteration from the point of the biggest bill
                    counter = biggestBillIndex.minus(1)
                }
            }
            counter = counter.plus(1)
        }

        if (billsToRemove.sum() == 0) {
            throw Exception("Sorry")
        }

        removeQuantities(billsToRemove)
        return billsToRemove
    }
}