/*
*
* Copyright (C) 2025 AxolotlDev and the Core contributors
*
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
* GNU General Public License for more details.
*
* You should have received a copy of the GNU General Public License
* along with this program. If not, see <https://www.gnu.org/licenses/>.
*
*/

package me.axolotldev.dbc.core.connect.internal

import me.axolotldev.dbc.abstracts.database.Connector
import me.axolotldev.dbc.abstracts.database.DatabaseInfo
import me.axolotldev.dbc.abstracts.database.DriverInfo
import me.axolotldev.dbc.abstracts.database.TransactionManager
import java.sql.CallableStatement
import java.sql.Connection
import java.sql.DriverManager
import java.sql.PreparedStatement
import java.sql.SQLException
import java.util.Properties
import kotlin.jvm.Throws

/**
 * An implementation of the [Connector] interface that manages the lifecycle
 * of a JDBC database connection, including auto-reconnection support.
 *
 * @property uri The generated JDBC URI string.
 * @property driver The driver info used to create the connection.
 * @property info The database connection credentials and metadata.
 * @property meta Additional configuration properties such as autoReconnect or retryTimes.
 * @property connection The active JDBC [Connection] instance, or null if not connected.
 */
@Suppress("unused")
class ConnectorImpl: Connector {

    val uri: String
    val driver: DriverInfo
    val info: DatabaseInfo
    val meta: Properties
    var connection: Connection? = null

    /**
     * Constructs a new [ConnectorImpl] with database info, driver and optional metadata.
     *
     * @param info The connection configuration.
     * @param driver The driver used to connect.
     * @param meta Optional metadata configuration. Defaults to an empty [Properties].
     */
    constructor(info: DatabaseInfo, driver: DriverInfo, meta: Properties = Properties()) {
        this.uri = driver.generateURI(info)
        this.driver = driver
        this.info = info
        this.meta = meta
    }

    /**
     * Establishes a new JDBC connection using the provided URI and credentials.
     *
     * @throws SQLException if the connection fails.
     * @throws IllegalStateException if the URI is invalid.
     */
    @Throws(SQLException::class)
    override fun connect() {
        if (!driver.isValidURI(uri)) {
            throw IllegalStateException("The generated URI fails its own validation.")
        }
        Class.forName(driver.driverAddress)
        connection = DriverManager.getConnection(uri, info.username, info.password)
    }

    /**
     * Executes a raw SQL query.
     *
     * Automatically attempts to reconnect if execution fails and `autoReconnect` is enabled.
     *
     * @param query The SQL query to be executed.
     * @return `true` if the execution returns a ResultSet, `false` otherwise.
     * @throws SQLException if the query fails or reconnection fails.
     */
    @Throws(SQLException::class)
    override fun execute(query: String): Boolean {
        requireConnected()
        try {
            connection?.createStatement().use { statement ->
                return statement?.execute(query) ?: false
            }
        } catch (ex: SQLException) {
            val rec = doReconnection()
            if (rec != null) throw rec
            return execute(query)
        }
    }

    /**
     * Prepares a [PreparedStatement] for the given SQL query.
     *
     * Automatically retries upon connection failure if `autoReconnect` is enabled.
     *
     * @param query The parameterized SQL query.
     * @return A prepared statement object.
     * @throws SQLException if preparation fails.
     */
    @Throws(SQLException::class)
    override fun preparedExecute(query: String): PreparedStatement {
        requireConnected()
        try {
            return connection?.prepareStatement(query)
                ?: throw IllegalStateException("Database is not connected")
        } catch (ex: SQLException) {
            val rec = doReconnection()
            if (rec != null) throw rec
            return preparedExecute(query)
        }
    }

    /**
     * Prepares a [CallableStatement] for stored procedure execution.
     *
     * Automatically retries on failure if `autoReconnect` is enabled.
     *
     * @param query The stored procedure or SQL call.
     * @return A callable statement object.
     * @throws SQLException if preparation fails.
     */
    @Throws(SQLException::class)
    override fun call(query: String): CallableStatement {
        requireConnected()
        try {
            return connection?.prepareCall(query)
                ?: throw IllegalStateException("Database is not connected")
        } catch (ex: SQLException) {
            val rec = doReconnection()
            if (rec != null) throw rec
            return call(query)
        }
    }

    /**
     * Returns the raw JDBC [Connection] object.
     *
     * @return the current connection instance, or `null` if not connected.
     */
    @Throws(SQLException::class)
    override fun getRaw(): Connection? {
        return connection
    }

    /**
     * Returns a [TransactionManager] implementation tied to the current connection.
     *
     * @return A transaction manager.
     * @throws IllegalStateException if the connection is not established.
     */
    @Throws(SQLException::class)
    override fun getTransactionManager(): TransactionManager {
        return TransactionManagerImpl(requireConnected())
    }

    /**
     * Closes the current connection if open, and sets it to `null`.
     *
     * @throws SQLException if an error occurs during closing.
     */
    @Throws(SQLException::class)
    override fun close() {
        try {
            connection?.let {
                if (!it.isClosed) it.close()
            }
        } finally {
            connection = null
        }
    }

    private fun requireConnected(): Connection {
        return connection ?: throw IllegalStateException("Database is not connected")
    }

    private fun doReconnection(): SQLException? {
        if (!(meta.getProperty("autoReconnect")?.equals("true", ignoreCase = true) ?: false)) {
            return null
        }

        var remain = meta.getProperty("retryTimes")?.toIntOrNull() ?: 1
        var lastException: SQLException? = null

        while (remain-- > 0) {
            try {
                close()
                connect()
                return null
            } catch (ex: SQLException) {
                lastException = ex
            }
        }

        return lastException
    }


}