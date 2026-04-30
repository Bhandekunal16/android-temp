package com.example.myapp.utils

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@Composable
fun @receiver:StringRes Int.str(): String = stringResource(this)
