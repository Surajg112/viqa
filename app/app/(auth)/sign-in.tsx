import {
  Button,
  Keyboard,
  KeyboardAvoidingView,
  Platform,
  ScrollView,
  StyleSheet,
  Text,
  View,
} from "react-native";
import React, { useState } from "react";
import { icons, url } from "@/constants";
import InputField from "@/components/InputField";
import { Link, router } from "expo-router";
import { LoginForm, Result } from "@/types/type";
import LoadingOverlay from "@/components/LoadingOverlay";

export default function SignIn() {
  const [form, setForm] = useState({
    emailId: "",
    password: "",
  });
  const [errors, setErrors] = useState<any>({});
  const [isLoading, setIsLoading] = useState(false);

  const validateForm = (form: LoginForm): boolean => {
    const newErrors: any = {};
    let isValid = true;

    if (!form.emailId.trim()) {
      newErrors.emailId = "Email is required";
      isValid = false;
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.emailId)) {
      newErrors.emailId = "Email is invalid";
      isValid = false;
    }

    if (!form.password) {
      newErrors.password = "Password is required";
      isValid = false;
    } else if (form.password.length < 8) {
      newErrors.password = "Password is least 8 characters";
      isValid = false;
    }

    setErrors(newErrors);
    return isValid;
  };
  const onSignInPress = async () => {
    Keyboard.dismiss();
    if (!validateForm(form)) return;
    setIsLoading(true);
    try {
      const response = await fetch(`${url}/auth/signin`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(form),
      });
      const result: Result = await response.json();
      setIsLoading(false);
      if (!result.flag) {
        console.error("Error response:", result);
        setIsLoading(false);
        alert(`Signin failed: ${result.message || "Unknown error"}`);
        return;
      }

      router.replace("/(tabs)/home");
    } catch (error) {
      setIsLoading(false);
      console.error("Signup error:", error);
      alert("Something went wrong. Please try again.");
    }
  };

  return (
    <>
      {isLoading && <LoadingOverlay overlayText="Signing In..." />}
      <KeyboardAvoidingView
        behavior={Platform.OS === "ios" ? "padding" : "height"}
        style={{ flex: 1, backgroundColor: "white" }}
        keyboardVerticalOffset={20}
      >
        <ScrollView style={styles.scrollView} keyboardDismissMode="on-drag">
          <View style={styles.container}>
            <View style={styles.titleWrapper}>
              <Text style={styles.titleText}>Welcome</Text>
            </View>
            <View style={styles.formWrapper}>
              <InputField
                label={"Email"}
                placeholder="Enter your email"
                placeholderTextColor="grey"
                icon={icons.email}
                value={form.emailId}
                inputMode="email"
                autoComplete="email"
                onChangeText={(value) => setForm({ ...form, emailId: value.trim().toLowerCase() })}
              />
              {errors.emailId ? (
                <Text style={styles.errorText}>{errors.emailId}</Text>
              ) : null}
              <InputField
                label={"Password"}
                placeholder="Enter your password"
                placeholderTextColor="grey"
                icon={icons.lock}
                secureTextEntry={true}
                value={form.password}
                onChangeText={(value) => setForm({ ...form, password: value.trim() })}
              />
              {errors.password ? (
                <Text style={styles.errorText}>{errors.password}</Text>
              ) : null}
            </View>

            <Button
              title="Log In"
              onPress={onSignInPress}
              // style={styles.signUpButton}
            />
          </View>
          <Link href={"/(auth)/sign-up"} style={styles.link}>
            <Text>{`Don't have an account? `}</Text>
            <Text style={styles.linkHighlight}>Sign Up</Text>
          </Link>
        </ScrollView>
      </KeyboardAvoidingView>
    </>
  );
}

const styles = StyleSheet.create({
  scrollView: {
    flexGrow: 1,
    backgroundColor: "white",
  },
  container: {
    flex: 1,
    backgroundColor: "white",
  },
  titleWrapper: {
    width: "100%",
    height: 300,
  },
  titleText: {
    position: "absolute",
    bottom: 20,
    left: 20,
    fontSize: 24,
    color: "black",
    fontWeight: "normal",
  },
  formWrapper: {
    padding: 20,
  },
  signUpButton: {
    marginTop: 24,
  },
  link: {
    marginTop: 40,
    textAlign: "center",
    flexDirection: "row",
    justifyContent: "center",
  },
  linkHighlight: {
    color: "#3b82f6",
  },
  errorText: {
    color: "red",
    marginTop: 10,
    marginBottom: 5,
  },
});
