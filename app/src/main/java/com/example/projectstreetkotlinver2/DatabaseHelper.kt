package com.example.projectstreetkotlinver2

import android.os.StrictMode
import android.util.Log
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.ResultSet

class DatabaseHelper {

    private val dbUrl = "jdbc:postgresql://51.250.123.240:5432/mydatabase"
    private val dbUser = "myuser"
    private val dbPassword = "mypassword"

    init {
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        checkConnection()
    }

    fun checkConnection() {
        var connection: Connection? = null
        try {
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)
            if (connection != null) {
                Log.i("DatabaseHelper", "Connection to the database established successfully.")
            } else {
                Log.e("DatabaseHelper", "Failed to establish connection to the database.")
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error while connecting to the database", e)
        } finally {
            try {
                connection?.close()
            } catch (e: Exception) {
                Log.e("DatabaseHelper", "Error closing connection", e)
            }
        }
    }

    fun addLook(username: String, image: ByteArray) {
        var connection: Connection? = null
        var preparedStatement: PreparedStatement? = null

        try {
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)
            val sql = "INSERT INTO mobile_look (username, image, created_at) VALUES (?, ?, ?)"
            preparedStatement = connection.prepareStatement(sql)
            preparedStatement.setString(1, username)
            preparedStatement.setBytes(2, image)
            preparedStatement.setTimestamp(3, java.sql.Timestamp(System.currentTimeMillis()))
            preparedStatement.executeUpdate()
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error adding look", e)
        } finally {
            try {
                preparedStatement?.close()
                connection?.close()
            } catch (e: Exception) {
                Log.e("DatabaseHelper", "Error closing resources", e)
            }
        }
    }

    fun getLooksByUsername(username: String): List<ByteArray> {
        val looks = mutableListOf<ByteArray>()
        var connection: Connection? = null
        var preparedStatement: PreparedStatement? = null
        var resultSet: ResultSet? = null

        try {
            connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword)
            val sql = "SELECT image FROM mobile_look WHERE username = ? ORDER BY created_at DESC"
            preparedStatement = connection.prepareStatement(sql)
            preparedStatement.setString(1, username)
            resultSet = preparedStatement.executeQuery()

            while (resultSet.next()) {
                val image = resultSet.getBytes("image")
                looks.add(image)
            }
        } catch (e: Exception) {
            Log.e("DatabaseHelper", "Error fetching looks", e)
        } finally {
            try {
                resultSet?.close()
                preparedStatement?.close()
                connection?.close()
            } catch (e: Exception) {
                Log.e("DatabaseHelper", "Error closing resources", e)
            }
        }

        return looks
    }
}
