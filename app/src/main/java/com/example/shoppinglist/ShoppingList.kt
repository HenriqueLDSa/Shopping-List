package com.example.shoppinglist

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class ShoppingItem(val id : Int,
                        var name : String,
                        var quantity : Int,
                        var isEditing : Boolean = false)

@Preview(showBackground = true)
@Composable
fun ShoppingListPreview(){
    ShoppingListApp()
}

@Composable
fun ShoppingListApp(){
    Box (modifier = Modifier.fillMaxSize()) {

        var sItems by remember { mutableStateOf(listOf<ShoppingItem>()) }
        var showDialog by remember { mutableStateOf(false) }
        var itemName by remember {mutableStateOf("")}
        var itemQuantity by remember {mutableStateOf("")}

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                items(sItems){
                    item ->
                        if (item.isEditing){
                            ShoppingItemEditor(item = item, onEditComplete =
                            {
                                editedName, editedQuantity ->
                                sItems = sItems.map { it.copy(isEditing = false) }
                                val editedItem = sItems.find { it.id == item.id }
                                editedItem?.let {
                                    it.name = editedName
                                    it.quantity = editedQuantity
                                }
                            })
                        }
                        else
                            ShoppingListItem(
                                item = item,
                                onEditClick = {
                                    sItems = sItems.map { it.copy(isEditing = it.id == item.id) }
                                },
                                onDeleteClick = {
                                    sItems -= item
                                }
                            )
                }
            }
        }
        Button(
            onClick = { showDialog = true },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 64.dp)
        ) {
            Text(text = "Add Item")
        }

        if(showDialog){
            AlertDialog(
                onDismissRequest = { showDialog = false },
                confirmButton =
                {
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween)
                    {
                        Button(onClick = { showDialog = false }) {
                            Text(text = "Cancel")
                        }

                        Button(onClick = {
                            if (itemName.isNotBlank())
                            {
                                val newItem = ShoppingItem(id = sItems.size+1, name = itemName, quantity = itemQuantity.toInt())
                                sItems += newItem
                                showDialog = false
                                itemName = ""
                                itemQuantity = ""
                            }
                        }) {
                            Text(text = "Add")
                        }
                    }
                },
                title = { Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "Add Shopping Item")
                }},
                text = {
                    Column {
                        OutlinedTextField(
                            value = itemName,
                            onValueChange = {itemName = it},
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )

                        OutlinedTextField(
                            value = itemQuantity,
                            onValueChange = {itemQuantity = it},
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                        )
                    }
                }
            )
        }
    }
}

@Composable
fun ShoppingListItem(item: ShoppingItem, onEditClick : () -> Unit, onDeleteClick : () -> Unit){
    Row (
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .border(
                border = BorderStroke(color = Color(0XFF018786), width = 1.dp),
                shape = RoundedCornerShape(25)
            ),
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Text(text = item.name, modifier = Modifier.padding(8.dp))
        Text(text = "Qty: ${item.quantity}", modifier = Modifier.padding(8.dp))
        Row (modifier = Modifier.padding(8.dp)){
            IconButton(onClick = onEditClick ) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "edit button")
            }
            IconButton(onClick = onDeleteClick ) {
                Icon(imageVector = Icons.Default.Delete, contentDescription = "delete button")
            }
        }
    }
}

@Composable
fun ShoppingItemEditor(item: ShoppingItem, onEditComplete: (String, Int) -> Unit){
    var editedName by remember { mutableStateOf(item.name) }
    var editedQuantity by remember { mutableStateOf(item.quantity.toString()) }
    var isEditing by remember { mutableStateOf(item.isEditing) }

    Row (modifier = Modifier
        .fillMaxWidth()
        .background(Color.Gray)
        .padding(8.dp), horizontalArrangement = Arrangement.SpaceEvenly){

        Column {
            BasicTextField(value = editedName, onValueChange = { editedName = it }, singleLine = true, modifier = Modifier
                .wrapContentSize()
                .padding(8.dp))
            BasicTextField(value = editedQuantity, onValueChange = { editedQuantity = it }, singleLine = true, modifier = Modifier
                .wrapContentSize()
                .padding(8.dp))
        }

        Button(onClick = {
            isEditing = false
            onEditComplete(editedName, editedQuantity.toIntOrNull() ?: 1)
        }
        ) {
            Text(text = "Save")
        }
    }
}