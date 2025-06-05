import { icons } from "@/constants";
import { InputFieldProps } from "@/types/type";
import React from "react";
import {
  Image,
  Keyboard,
  KeyboardAvoidingView,
  Platform,
  StyleSheet,
  Text,
  TextInput,
  TouchableWithoutFeedback,
  View,
} from "react-native";

export default function InputField({
  label,
  icon,
  secureTextEntry = false,
  containerStyle,
  inputStyle,
  iconStyle,
  className,
  ...props
}: InputFieldProps) {
    return (
        <KeyboardAvoidingView
          behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
        >
          <TouchableWithoutFeedback onPress={Keyboard.dismiss}>
            <View style={styles.wrapper}>
              <Text style={styles.label}>{label}</Text>
              <View style={styles.inputContainer}>
                {icon && <Image source={icon} style={styles.icon} />}
                <TextInput
                  style={styles.input}
                  secureTextEntry={secureTextEntry}
                  {...props}
                />
              </View>
            </View>
          </TouchableWithoutFeedback>
        </KeyboardAvoidingView>
      );
    }
    
    const styles = StyleSheet.create({
      wrapper: {
        marginVertical: 8,
        width: '100%',
      },
      label: {
        fontSize: 16,
        marginBottom: 12,
      },
      inputContainer: {
        flexDirection: 'row',
        justifyContent: 'flex-start',
        alignItems: 'center',
        position: 'relative',
        backgroundColor: '#f5f5f5', // neutral-100
        borderRadius: 9999,
        borderWidth: 1,
        borderColor: '#f5f5f5',
      },
      icon: {
        width: 24,
        height: 24,
        marginLeft: 16,
      },
      input: {
        borderRadius: 9999,
        padding: 16,
        fontSize: 15,
        flex: 1,
        textAlign: 'left',
      },
    });