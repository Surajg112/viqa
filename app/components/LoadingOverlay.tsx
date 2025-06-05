import { View, Text, ActivityIndicator, StyleSheet } from "react-native";
import React from "react";
import { LoadingOverlayProps } from "@/types/type";


export default function LoadingOverlay({ overlayText }: LoadingOverlayProps) {
  return (
    <View style={styles.overlayWrapper}>
      <View style={styles.box}>
        <ActivityIndicator size="large" color="#114664" />
        <Text style={styles.loadingText}>{overlayText}</Text>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  overlayWrapper: {
    position: "absolute",
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: "rgba(0,0,0,0.3)",
    justifyContent: "center",
    alignItems: "center",
    zIndex: 10,
  },
  box: {
    width: 200,
    backgroundColor: "#fff",
    borderRadius: 12,
    padding: 24,
    alignItems: "center",
    shadowColor: "#000",
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.25,
    shadowRadius: 6,
    elevation: 6,
  },
  loadingText: {
    marginTop: 12,
    fontSize: 16,
    color: "#114664",
    textAlign: "center",
  },
});
