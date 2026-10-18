package nl.dionsegijn.samples.multiplatform

import androidx.compose.ui.window.ComposeUIViewController
import platform.UIKit.UIViewController

// Shown by iosApp, see ContentView.swift
fun mainViewController(): UIViewController = ComposeUIViewController { App() }
