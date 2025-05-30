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

import me.axolotldev.dbc.abstracts.database.TransactionManager
import java.sql.Connection
import java.sql.SQLException

/**
 * An implementation of [TransactionManager] responsible for managing
 * database transactions on a given [Connection] instance.
 *
 * This class allows starting, committing, and rolling back transactions,
 * providing a clean abstraction over JDBC transaction handling.
 *
 * @property conn The active [Connection] on which transactions are managed.
 * @throws IllegalStateException if the provided connection is already closed.
 */
@Suppress("unused")
class TransactionManagerImpl : TransactionManager {

    val conn: Connection

    /**
     * Constructs a new [TransactionManagerImpl] with a given [Connection].
     *
     * @param conn A valid and open JDBC [Connection] object.
     * @throws IllegalStateException if the connection is already closed.
     */
    constructor(conn: Connection) {
        require(!conn.isClosed) { "Database is not connected" }
        this.conn = conn
    }

    /**
     * Begins a transaction by disabling auto-commit mode.
     *
     * @throws SQLException if an error occurs while changing the auto-commit state.
     */
    @Throws(SQLException::class)
    override fun begin() {
        conn.autoCommit = false
    }

    /**
     * Commits the current transaction and re-enables auto-commit mode.
     *
     * @throws SQLException if the commit fails.
     */
    @Throws(SQLException::class)
    override fun commit() {
        conn.commit()
        conn.autoCommit = true
    }

    /**
     * Rolls back the current transaction.
     *
     * @throws SQLException if the rollback fails.
     */
    @Throws(SQLException::class)
    override fun rollback() {
        conn.rollback()
    }
}
