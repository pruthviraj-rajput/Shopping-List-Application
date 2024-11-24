package com.pnr.shoppinglistapplication.ui.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items


data class ShoppingItem(val id: Int, var name: String, var quantity: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun shoppingListApplication() {
    var shoppingItems by remember { mutableStateOf(listOf<ShoppingItem>()) }
    var showDialog by remember { mutableStateOf(false) }
    var itemName by remember { mutableStateOf("") }
    var itemQuantity by remember { mutableStateOf("") }
    var showErrorDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = { showDialog = true },
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text(text = "ENTER ITEM")
        }
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            items(shoppingItems) { item ->
                ShoppingListItems(
                    item = item,
                    onEditClick = { editedName, editedQuantity ->
                        shoppingItems = shoppingItems.map {
                            if (it.id == item.id) it.copy(name = editedName, quantity = editedQuantity) else it
                        }
                    },
                    onDeleteClick = {
                        shoppingItems = shoppingItems.filter { it.id != item.id }
                    }
                )
            }
        }
    }
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            confirmButton = {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(onClick = { showDialog = false }) {
                        Text(text = "CANCEL")
                    }
                    Button(onClick = {
                        if (itemName.isNotBlank() && itemQuantity.isNotBlank()) {
                            val quantity = itemQuantity.toIntOrNull()
                            if (quantity != null) {
                                val newItem = ShoppingItem(
                                    id = shoppingItems.size + 1,
                                    name = itemName,
                                    quantity = quantity
                                )
                                shoppingItems = shoppingItems + newItem
                                showDialog = false
                                itemName = ""
                                itemQuantity = ""
                            } else {
                                showErrorDialog = true
                            }
                        }
                    }) {
                        Text(text = "ADD")
                    }
                }
            },
            title = { Text(text = "ADD ITEM FOR SHOP") },
            text = {
                Column {
                    OutlinedTextField(
                        value = itemName,
                        onValueChange = { itemName = it },
                        label = { Text(text = "ADD ITEM NAME") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = itemQuantity,
                        onValueChange = { itemQuantity = it },
                        label = { Text(text = "QUANTITY") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        )
    }

    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            confirmButton = {
                Button(onClick = { showErrorDialog = false }) {
                    Text(text = "OK")
                }
            },
            title = { Text(text = "Invalid Input") },
            text = { Text(text = "Please enter a valid quantity.") }
        )
    }
}

@Composable
fun ShoppingListItems(
    item: ShoppingItem,
    onEditClick: (String, Int) -> Unit,
    onDeleteClick: () -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var editedName by remember { mutableStateOf(item.name) }
    var editedQuantity by remember { mutableStateOf(item.quantity.toString()) }
    var showErrorDialog by remember { mutableStateOf(false) } // State for error dialog

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(
                border = BorderStroke(2.dp, Color.Black),
                shape = RoundedCornerShape(20)
            )
            .padding(8.dp)
    ) {
        if (isEditing) {
            Column(modifier = Modifier.weight(1f)) {
                OutlinedTextField(
                    value = editedName,
                    onValueChange = { editedName = it },
                    label = { Text(text = "Edit Name") },
                    singleLine = true
                )
                OutlinedTextField(
                    value = editedQuantity,
                    onValueChange = { editedQuantity = it },
                    label = { Text(text = "Edit Quantity") },
                    singleLine = true
                )
            }
            Button(
                onClick = {
                    val quantity = editedQuantity.toIntOrNull()
                    if (quantity != null) {
                        isEditing = false
                        onEditClick(editedName, quantity)
                    } else {
                        // Show error dialog if quantity is invalid
                        showErrorDialog = true
                    }
                },
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(text = "OK")
            }
        } else {
            Text(text = item.name, modifier = Modifier.weight(1f))
            Text(text = "Quantity: ${item.quantity}", modifier = Modifier.weight(1f))

            IconButton(onClick = { isEditing = true }) {
                Icon(imageVector = Icons.Default.Create, contentDescription = "Edit")
            }
            IconButton(onClick = onDeleteClick) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            confirmButton = {
                Button(onClick = { showErrorDialog = false }) {
                    Text(text = "OK")
                }
            },
            title = { Text(text = "Invalid Quantity") },
            text = { Text(text = "Please enter a valid numeric quantity.") }
        )
    }
}
