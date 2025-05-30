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

package me.axolotldev.dbc.core.connect

import me.axolotldev.dbc.abstracts.database.Connector
import me.axolotldev.dbc.abstracts.database.DatabaseInfo
import me.axolotldev.dbc.abstracts.database.DriverInfo
import me.axolotldev.dbc.core.connect.internal.ConnectorImpl
import java.util.Properties

/**
 * A builder class for creating instances of [Connector].
 *
 * This class encapsulates all necessary configuration elements required
 * to establish a database connection, including database info, driver info,
 * and optional metadata properties.
 *
 * It provides a fluent way to construct a [Connector] instance,
 * which can then be used to interact with a database.
 *
 * @property info The [DatabaseInfo] object containing database connection details (URL, username, password).
 * @property driver The [DriverInfo] object responsible for generating connection URIs and driver class names.
 * @property meta Optional [Properties] providing additional metadata or connection options.
 *
 * @constructor Creates a new builder with the given database info, driver, and optional metadata.
 */
@Suppress("unused")
data class ConnectBuilder @JvmOverloads constructor(
    var info: DatabaseInfo,
    var driver: DriverInfo,
    var meta: Properties = Properties()
) {
    /**
     * Builds and returns a new [Connector] using the current configuration.
     *
     * @return A fully constructed [Connector] instance.
     */
    fun build(): Connector {
        return ConnectorImpl(info, driver, meta)
    }
}

