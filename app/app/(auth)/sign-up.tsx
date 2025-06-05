import {
  Button,
  Keyboard,
  KeyboardAvoidingView,
  Platform,
  ScrollView,
  StyleSheet,
  Text,
  TouchableWithoutFeedback,
  View,
} from "react-native";
import React, { useEffect, useState } from "react";
import { icons, url } from "@/constants";
import InputField from "@/components/InputField";
import { Link, router } from "expo-router";
import ProfilePicker from "@/components/ProfilePicker";
import OtpModal from "@/components/OtpModal";
import { Result, SignUpForm } from "@/types/type";
import LoadingOverlay from "@/components/LoadingOverlay";

export default function SignUp() {
  const [form, setForm] = useState({
    firstName: "",
    lastName: "",
    emailId: "",
    password: "",
    birthDate: null as Date | null,
    gender: "",
  });
  const [errors, setErrors] = useState<any>({});
  const [loading, setLoading] = useState(false);

  const validateForm = (form: SignUpForm): boolean => {
    const newErrors: any = {};
    let isValid = true;

    // First Name validation
    if (!form.firstName.trim()) {
      newErrors.firstName = "First name is required";
      isValid = false;
    }

    // Last Name validation
    if (!form.lastName.trim()) {
      newErrors.lastName = "Last name is required";
      isValid = false;
    }

    // Email validation
    if (!form.emailId.trim()) {
      newErrors.emailId = "Email is required";
      isValid = false;
    } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(form.emailId)) {
      newErrors.emailId = "Email is invalid";
      isValid = false;
    }

    // Password validation
    if (!form.password) {
      newErrors.password = "Password is required";
      isValid = false;
    } else if (form.password.length < 8) {
      newErrors.password = "Password must be at least 8 characters";
      isValid = false;
    }

    // Birth Date validation
    if (!form.birthDate) {
      newErrors.birthDate = "Birth date is required";
      isValid = false;
    } else {
      const today = new Date();
      const birthDate = new Date(form.birthDate);
      const ageDiff = today.getTime() - birthDate.getTime();
      const ageDate = new Date(ageDiff);
      const age = Math.abs(ageDate.getUTCFullYear() - 1970);

      if (age < 13) {
        newErrors.birthDate = `You must be at least 13 years old`;
        isValid = false;
      }
    }

    // Gender validation
    if (!form.gender) {
      newErrors.gender = "Gender is required";
      isValid = false;
    }

    setErrors(newErrors);
    return isValid;
  };

  function formatDateToLocalString(date: Date): string {
    const year = date.getFullYear();
    const month = `${date.getMonth() + 1}`.padStart(2, "0");
    const day = `${date.getDate()}`.padStart(2, "0");
    return `${year}-${month}-${day}`;
  }

  const [showOtpModal, setShowOtpModal] = useState(false);

  const onSignUpPress = async () => {
    Keyboard.dismiss();
    if (!validateForm(form)) return;
    const payload = {
      ...form,
      birthDate: form.birthDate
        ? formatDateToLocalString(form.birthDate)
        : null,
    };
    setLoading(true);
    try {
      const response = await fetch(`${url}/auth/signup`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify(payload),
      });
      const result: Result = await response.json();

      if (!result.flag) {
        console.error("Error response:", result.message);
        setLoading(false);
        alert(`Signup failed: ${result.message || "Unknown error"}`);
        return;
      }
      // router.replace("")
      console.log(JSON.stringify(result));
      setLoading(false);
      setShowOtpModal(true);

      // Optional: navigate to sign-in or home page
      // navigation.navigate('SignIn');
    } catch (error) {
      setLoading(false);
      console.error("Signup error:", error);
      alert("Something went wrong. Please try again.");
    }
  };

  const handleOtpSuccess = () => {
    setShowOtpModal(false);
    router.replace("/(tabs)/home");
  };
  return (
    <>
      {loading && <LoadingOverlay overlayText={"Creating your account...."} />}
      <TouchableWithoutFeedback onPress={Keyboard.dismiss} accessible={false}>
        <KeyboardAvoidingView
          behavior={Platform.OS === "ios" ? "padding" : "height"}
          style={{ flex: 1, backgroundColor: "#ffffff" }}
          keyboardVerticalOffset={20}
        >
          <ScrollView style={styles.scrollView} keyboardShouldPersistTaps="handled">
            <View style={styles.container}>
              <View style={styles.titleWrapper}>
                <Text style={styles.titleText}>Create Your Account</Text>
              </View>
              <View style={styles.formWrapper}>
                <InputField
                  label={"First Name"}
                  placeholder="Enter your first name"
                  placeholderTextColor="grey"
                  icon={icons.person}
                  value={form.firstName}
                  onChangeText={(value) =>
                    setForm({ ...form, firstName: value.trim() })
                  }
                />
                {errors.firstName ? (
                  <Text style={styles.errorText}>{errors.firstName}</Text>
                ) : null}
                <InputField
                  label={"Last Name"}
                  placeholder="Enter your last name"
                  placeholderTextColor="grey"
                  icon={icons.person}
                  value={form.lastName}
                  onChangeText={(value) =>
                    setForm({ ...form, lastName: value.trim() })
                  }
                />
                {errors.lastName ? (
                  <Text style={styles.errorText}>{errors.lastName}</Text>
                ) : null}
                <InputField
                  label={"Email"}
                  placeholder="Enter your email"
                  placeholderTextColor="grey"
                  icon={icons.email}
                  value={form.emailId}
                  onChangeText={(value) =>
                    setForm({ ...form, emailId: value.trim().toLowerCase() })
                  }
                  inputMode="email"
                  autoComplete="email"
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
                  onChangeText={(value) =>
                    setForm({ ...form, password: value.trim() })
                  }
                />
                {errors.password ? (
                  <Text style={styles.errorText}>{errors.password}</Text>
                ) : null}
              </View>
              <ProfilePicker
                onChange={({ gender, birthdate }) => {

                  const updatedForm = { ...form, gender, birthDate: birthdate };
                  setForm(updatedForm);
                  console.log("Updated immediately:", updatedForm);
                }}
              />
              {errors.gender || errors.birthDate ? (
                <View style={styles.profilePickerError}>
                  <Text style={styles.errorText}>
                    {errors.gender}. {errors.birthDate}
                  </Text>
                </View>
              ) : null}
              <Button
                title="Sign Up"
                onPress={onSignUpPress}
                // style={styles.signUpButton}
                disabled={loading}
              />
            </View>
            <Link href={"/(auth)/sign-in"} style={styles.link}>
              <Text>Already have an account? </Text>
              <Text style={styles.linkHighlight}>Log In</Text>
            </Link>
          </ScrollView>
          <OtpModal
            visible={showOtpModal}
            emailId={form.emailId}
            onClose={() => setShowOtpModal(false)}
            onSuccess={handleOtpSuccess}
          />
        </KeyboardAvoidingView>
      </TouchableWithoutFeedback>
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
    position: "relative",
    width: "100%",
    height: 150,
  },
  image: {
    zIndex: 0,
    width: "100%",
    height: 250,
  },
  titleText: {
    position: "absolute",
    bottom: 20,
    left: 20,
    fontSize: 24, // text-2xl ~ 24px
    color: "black",
    fontWeight: "normal",
  },
  formWrapper: {
    padding: 20,
  },
  signUpButton: {
    marginTop: 24, // mt-6 = 6 * 4 = 24 px
  },
  link: {
    marginTop: 40, // mt-10 = 10 * 4 = 40px
    textAlign: "center",
    flexDirection: "row",
    justifyContent: "center",
    marginBottom: 40,
  },
  linkHighlight: {
    color: "#3b82f6", // Tailwind's blue-500
  },
  inputContainer: {
    marginVertical: 8,
    marginBottom: 20,
    zIndex: 0, // Ensure proper stacking for date picker modal
  },
  label: {
    fontSize: 18,
    marginBottom: 12,
    fontWeight: "400",
    color: "black",
  },
  datePickerButton: {
    flexDirection: "row",
    alignItems: "center",
    backgroundColor: "#f5f5f5",
    borderRadius: 9999,
    paddingVertical: 12,
    paddingHorizontal: 15,
  },
  dateText: {
    fontSize: 16,
    color: "#000000",
    padding: 7,
    textAlign: "left",
    paddingLeft: 16,
  },
  iconContainer: {
    marginLeft: 6,

    alignItems: "center",
  },
  icon: {
    width: 20,
    height: 20,
    tintColor: "#a3a3a3",
  },
  // Date Picker Modal Styles for iOS
  datePickerContainer: {
    backgroundColor: "white",
    position: "absolute",
    bottom: 0,
    left: 0,
    right: 0,
    borderWidth: 0.5,
    borderColor: "#ccc",
    borderRadius: 10,
    shadowOffset: {
      width: 2,
      height: 2,
    },
    shadowOpacity: 0.1,
    zIndex: 1000,
  },
  datePickerHeader: {
    flexDirection: "row",
    justifyContent: "space-between",
    padding: 10,
    backgroundColor: "#f8f8f8",
    borderBottomWidth: 1,
    borderBottomColor: "#ccc",
  },
  datePickerCancel: {
    color: "#ff4040",
    fontSize: 16,
  },
  datePickerDone: {
    color: "#496ad4",
    fontSize: 16,
    fontWeight: "500",
  },
  // Gender Selector Styles
  genderButton: {
    flexDirection: "row",
    alignItems: "center",
    borderWidth: 1,
    borderColor: "#ccc",
    borderRadius: 8,
    height: 50,
    paddingLeft: 15,
  },
  genderPickerWrapper: {
    flex: 1,
    height: 50,
  },
  iosPicker: {
    flex: 1,
    height: 50,
    marginLeft: -10, // Adjust to align text with other fields
  },
  androidPicker: {
    flex: 1,
    height: 50,
    width: "100%",
    color: "#333",
  },
  genderPickerContainer: {
    margin: 20,
    padding: 10,
    backgroundColor: "#e6e6e6",
    borderRadius: 10,
  },
  genderLabel: {
    fontSize: 16,
    color: "#333333",
    marginBottom: 8,
  },
  genderPicker: {
    height: 50,
    backgroundColor: "#d3d3d3",
    borderRadius: 5,
  },
  genderPickerItem: {
    color: "#000000",
    fontSize: 16,
  },
  errorText: {
    color: "red",
    marginTop: 10,
    marginBottom: 5,
  },
  profilePickerError: {
    paddingHorizontal: 20,
    paddingVertical: 0,
  },
});
