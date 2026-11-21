package com.wakemate.ai.ui.screens.chat

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.wakemate.ai.data.local.entity.ChatMessageEntity
import com.wakemate.ai.ui.components.CircularCountdown
import com.wakemate.ai.ui.components.NeonCard
import com.wakemate.ai.ui.theme.*
import com.wakemate.ai.util.DateTimeUtils

@Composable
fun ChatScreen(
    viewModel: ChatViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Auto-scroll to bottom on new message
    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        // Chat Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(NeonPurple, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Filled.AutoAwesome,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "WakeMate Assistant",
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "English • Urdu • Roman Urdu",
                        color = NeonCyan,
                        fontSize = 11.sp
                    )
                }
            }

            IconButton(onClick = { viewModel.clearChat() }) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Clear Chat",
                    tint = TextMuted,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Divider(color = DarkCardBorder, thickness = 1.dp)

        // 10-Second Wake-Up Verification Countdown Card
        AnimatedVisibility(
            visible = state.isVerificationActive,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            NeonCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                glowAccent = NeonPink,
                backgroundColor = DarkSurfaceVariant
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularCountdown(
                        totalSeconds = 10,
                        remainingSeconds = state.remainingCountdownSeconds,
                        size = 64.dp,
                        strokeWidth = 5.dp
                    )

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Wake-Up Verification Active!",
                            color = NeonPink,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Confirm you are awake before time expires or Charging Challenge starts!",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        }

        // Messages List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(state.messages) { message ->
                MessageBubble(message = message)
            }

            if (state.isAiThinking) {
                item {
                    Text(
                        text = "WakeMate is thinking... ✍️",
                        color = NeonCyan,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 12.dp, top = 4.dp)
                    )
                }
            }
        }

        // Quick Reply Chips
        if (state.quickReplies.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.quickReplies) { reply ->
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .clickable { viewModel.sendMessage(reply) },
                        color = DarkSurfaceVariant,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text(
                            text = reply,
                            color = NeonCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Input Field & Send Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 80.dp, top = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = {
                    Text(
                        text = "Ask anything or say 'Mujhe 6 ghante baad study yaad dilana'...",
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                },
                maxLines = 3,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = NeonCyan,
                    unfocusedBorderColor = DarkCardBorder,
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface
                ),
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        viewModel.sendMessage(inputText)
                        inputText = ""
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(NeonCyan, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Filled.Send,
                    contentDescription = "Send",
                    tint = DarkBg,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun MessageBubble(message: ChatMessageEntity) {
    val isUser = message.sender.uppercase() == "USER"

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Surface(
            color = if (isUser) NeonPurple else DarkSurfaceVariant,
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            modifier = Modifier.widthIn(max = 290.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = message.text,
                    color = if (isUser) Color.White else TextPrimary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = DateTimeUtils.formatMessageTime(message.timestamp),
                    color = if (isUser) Color.White.copy(alpha = 0.7f) else TextMuted,
                    fontSize = 10.sp,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}
