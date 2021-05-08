package com.devhypercoder.securebrowser.data

fun composeHistoryPage(historyItems: ArrayList<HistoryItem>): String {
    return """
            <style>
                h1{
                    font-size: 6rem;
                    text-align: center;
                }
                .card{
                    display:flex;
                    justify-content: space-between;
                    padding: 2rem;
                    flex-direction: row;
                    align-items: center;
                }
            </style>
            
            <h1>History</h1>
            
            ${
        historyItems.joinToString {
            """
                <div>
                ${it.toHtml()}
                </div>
                """.trimIndent()
        }
    }
        """.trimIndent()

}

fun handleHistoryCommand(): String {

    val historyItems = ArrayList<HistoryItem>()
    historyItems.add(HistoryItem("DevHyperCoder", "devhypercoder.com", 0))
    historyItems.add(HistoryItem("GitHub", "https://github.com/devhypercoder/securebrowser", 1))
    return composeHistoryPage(historyItems)
}

data class HistoryItem(val name: String, val url: String, val id: Int) {
    fun toHtml(): String {
        return """
                <div class="card" id="$id">
                    <div>
                        <h3>$name</h3>
                        <p>$url</p>
                    <div>
                        <a href="$url">GO</a>
                    </div>
                </div>
            """.trimIndent()
    }
}
