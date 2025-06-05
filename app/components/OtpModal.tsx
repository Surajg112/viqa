import { url } from "@/constants";
import { OtpModalProps } from "@/types/type";
import React, { useEffect, useState } from "react";
import {
  Modal,
  View,
  Text,
  TextInput,
  Button,
  TouchableOpacity,
  StyleSheet,
} from "react-native";
import LoadingOverlay from "./LoadingOverlay";

export default function OtpModal({
  visible,
  emailId,
  onClose,
  onSuccess,
}: OtpModalProps) {
  const [otp, setOtp] = useState("");
  const [timer, setTimer] = useState(61);
  const [resentOtp, setResentOtp] = useState(false);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (visible) {
      setTimer(61);
      setOtp("");
    }
  }, [visible]);

  useEffect(() => {
    let interval: NodeJS.Timeout | number;
    if (visible && timer > 0) {
      interval = setInterval(() => {
        setTimer((t) => t - 1);
      }, 1000);
    }
    return () => clearInterval(interval);
  }, [visible, timer]);

  const onVerify = async () => {
    setLoading(true);
    try {
      const response = await fetch(`${url}/auth/verify-otp`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ emailId, otp }),
      });
      const data = await response.toString();
      if (!response.ok) {
        setLoading(false);
        alert(data || "Invalid OTP");
        return;
      }
      setLoading(false);
      alert("OTP verified!");
      onSuccess();
    } catch (err) {
      setLoading(false);
      alert("Verification failed.");
      console.error(err);
    }
  };

  const onResend = async () => {
    setResentOtp(true);
    try {
      const response = await fetch(
        `${url}/auth/resend-otp?emailId=${encodeURIComponent(emailId)}`,
        {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          // No body needed since we're sending as query parameter
        }
      );
      const data = await response.toString();
      if (!response.ok) {
        setResentOtp(false);
        alert(data || "Failed to resend OTP");
        return;
      }
      setResentOtp(false);
      alert("OTP resent!");
      setTimer(100);
    } catch (err) {
      setResentOtp(false);
      alert("Error resending OTP.");
      console.error(err);
    }
  };

  return (
    <>
      <Modal visible={visible} transparent animationType="slide">
        {resentOtp && <LoadingOverlay overlayText="Resending OTP..." />}
        {loading && <LoadingOverlay overlayText="Verifying OTP..." />}
        <View style={styles.overlay}>
          <View style={styles.modal}>
            <Text style={styles.title}>Enter the OTP sent to your email</Text>
            <TextInput
              style={styles.input}
              value={otp}
              onChangeText={setOtp}
              keyboardType="numeric"
              placeholder="Enter OTP"
              placeholderTextColor={"#d9d9d9"}
            />
            <Button title="Verify OTP" onPress={onVerify} />
            <View style={styles.buttonContainer}>
              {timer <= 0 ? (
                <TouchableOpacity onPress={onResend}>
                  <Text style={styles.resend}>Resend OTP</Text>
                </TouchableOpacity>
              ) : (
                <Text style={styles.timer}>Resend in {timer}s</Text>
              )}

              <TouchableOpacity onPress={onClose}>
                <Text style={styles.close}>Cancel</Text>
              </TouchableOpacity>
            </View>
          </View>
        </View>
      </Modal>
    </>
  );
}

const styles = StyleSheet.create({
  overlay: {
    flex: 1,
    justifyContent: "center",
    backgroundColor: "rgba(0,0,0,0.4)",
  },
  modal: {
    margin: 20,
    backgroundColor: "white",
    borderRadius: 10,
    padding: 40,
    alignItems: "center",
  },
  title: { fontSize: 18, marginBottom: 10 },
  input: {
    width: "80%",
    borderBottomWidth: 1,
    marginBottom: 15,
    fontSize: 20,
    fontWeight: 500,
    textAlign: "center",
    padding: 20,
    letterSpacing: 3,
  },
  buttonContainer: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
    gap: "40%",
    marginTop: 10,
  },

  resend: {
    color: "blue",
    marginTop: 10,
    fontSize: 14,
  },

  timer: {
    color: "gray",
    marginTop: 10,
  },

  close: {
    color: "red",
    marginTop: 10,
    marginLeft: 20,
  },
});
