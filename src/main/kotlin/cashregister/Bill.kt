package cashregister

/**
 * A class to hold the dollar amount for a type of bill and the quantity
 *
 * @param dollarAmount Amount it is worth ($20 -> 20 as parameter)
 * @param quantity Quantity of bills. Defaults to 0 if not specified.
 */
class Bill(val dollarAmount: Int, var quantity: Int = 0) {
    fun add(amount: Int = 1) {
        quantity += amount
    }

    fun remove(amount: Int = 1) {
        if (amount > quantity) {
            throw Exception("Cannot remove more than what you have")
        }
        quantity -= amount
    }

    fun calculateTotal(): Int {
        return dollarAmount.times(quantity)
    }
}