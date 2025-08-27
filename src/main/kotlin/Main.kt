package io.github.xkaih

fun printAccounts(accountHandler: AccountHandler): ConsoleHandler{
    accountHandler.getDecryptedAccounts().toSortedMap().forEach { (id, accountData) ->
        ConsoleHandler.printWithColor("${id}. ", AnsiColor.CYAN)
            .printWithColor("[${accountData.name}] ", AnsiColor.RED)
            .printWithColor("${accountData.email} ", AnsiColor.RED)
            .printlnWithColor("${accountData.cipherPassword} ", AnsiColor.BLUE)
            .lineBreak()
    }
    return ConsoleHandler
}

fun main() {
    ConsoleHandler.printlnWithColor("WELCOME TO THE CONSOLE PASSWORD MANAGER", AnsiColor.RED).lineBreak()
    ConsoleHandler.printWithColor("Please, enter your password: ", AnsiColor.YELLOW)

    //This should be cleared once is used btw, but like AccountHandler pwd, ill keep like this.
    // Also, the pwd shouldnt be visible when writing
    val password = readln()

    val accounHandler = AccountHandler(password)

    while (true) {
        ConsoleHandler.clearConsole()
        if (!accounHandler.getDecryptedAccounts().isEmpty())
            printAccounts(accounHandler)
        else
            ConsoleHandler.printlnWithColor("NO ACCOUNTS HAVE BEEN ADDED YET", AnsiColor.RED).lineBreak()

        ConsoleHandler.printlnWithColor("[1] Add account", AnsiColor.YELLOW)
            .printlnWithColor("[2] Modify account", AnsiColor.YELLOW)
            .printlnWithColor("[3] Delete account", AnsiColor.YELLOW)
            .lineBreak()
            .printWithColor("Select an option: ", AnsiColor.PURPLE)

        val option = readlnOrNull()?.toIntOrNull()?.takeIf { it in 1..3 }


        when (option) {
            null -> {
                ConsoleHandler.printlnWithColor("ERROR! NOT CORRECT OPTION WAS SELECTED", AnsiColor.RED)
                return
            }

            1 -> {
                var name = ""
                var email = ""
                var password = ""
                ConsoleHandler.printWithColor("Name: ", AnsiColor.YELLOW)
                    .readLineInto { name = it.toString() }
                    .printWithColor("Email: ", AnsiColor.YELLOW)
                    .readLineInto { email = it.toString() }
                    .printWithColor("Password: ", AnsiColor.YELLOW)
                    .readLineInto { password = it.toString() }

                accounHandler.addAccount(AccountData(name, email, password))
            }
            2 -> {
                var name = ""
                var email = ""
                var password = ""
                var id = 0
                printAccounts(accounHandler)
                    .lineBreak()
                    .printlnWithColor("Id: ", AnsiColor.YELLOW)
                    .readLineInto { id = it?.toInt() ?: -1 }
                    .printWithColor("Name: ", AnsiColor.YELLOW)
                    .readLineInto { name = it.toString() }
                    .printWithColor("Email: ", AnsiColor.YELLOW)
                    .readLineInto { email = it.toString() }
                    .printWithColor("Password: ", AnsiColor.YELLOW)
                    .readLineInto { password = it.toString() }

                accounHandler.modifyAccount(id,name,email,password)
            }
            3 -> {
                var id = 0
                printAccounts(accounHandler)
                    .lineBreak()
                    .printlnWithColor("Id: ", AnsiColor.YELLOW)
                    .readLineInto { id = it?.toInt() ?: -1 }

                accounHandler.deleteAccount(id)
            }
        }
        accounHandler.saveChanges()
    }

}