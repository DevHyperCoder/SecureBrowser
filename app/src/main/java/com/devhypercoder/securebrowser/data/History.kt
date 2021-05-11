package com.devhypercoder.securebrowser.data

import androidx.room.*

fun composeHistoryPage(histories: Array<History>): String {
    return """
            <style>
            *{
            background: #23232e;
            color: white;
            }
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
        histories.joinToString {
            """
                <div>
                ${it.toHtml()}
                </div>
                """.trimIndent()
        }
    }
        """.trimIndent()

}


@Entity
data class History(
    val name: String,
    val url: String,
    @PrimaryKey(autoGenerate = true) val id: Int? = null
) {
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

@Dao
interface HistoryDao {
    @Insert
    suspend fun insertHistory(history: History)

    @Query("SELECT * from history")
    suspend fun getFullHistory(): Array<History>
}