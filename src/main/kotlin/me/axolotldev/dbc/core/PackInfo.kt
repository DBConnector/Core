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

package me.axolotldev.dbc.core

/**
 * Holds package metadata information for the DBConnector-Core library.
 *
 * This object contains constants describing the package name, version,
 * description, author, and contact email.
 */
@Suppress("unused")
object PackInfo {

    /** The name of the package */
    const val PACK_NAME = "DBConnector-Core"

    /** The current version of the package */
    const val PACK_VERSION = "1.0.0"

    /** A short description of the package */
    const val DESCRIPTION = "Core component responsible for managing database connections and executing SQL commands across multiple database drivers."

    /** The author or maintainer of the package */
    const val AUTHOR = "AxolotlDev"

    /** Contact email address for package-related inquiries */
    const val CONTACT_EMAIL = "dbc@pkg-contact.axolotldev.me"

}

