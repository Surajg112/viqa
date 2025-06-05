import { TextInputProps } from "react-native";
declare interface InputFieldProps extends TextInputProps {
    label: string;
    icon?: any;
    secureTextEntry?: boolean;
    labelStyle?: string;
    containerStyle?: string;
    inputStyle?: string;
    iconStyle?: string;
    className?: string;
}

declare interface ProfileData {
    gender: string;
    birthdate: Date | null;
}

declare interface ProfilePickerProps {
    onChange?: (data: ProfileData) => void;
}

declare interface OtpModalProps {
  visible: boolean;
  emailId: string;
  onClose: () => void;
  onSuccess: () => void;
}

declare interface Result {
    flag: boolean;
    code: number;
    message: string;
    data?: any; 
}

declare interface LoadingOverlayProps {
    overlayText : string
}

declare interface SignUpForm {
  firstName: string;
  lastName: string;
  emailId: string;
  password: string;
  birthDate: Date | null;
  gender: string;
}

declare interface LoginForm {
    emailId: string;
    password: string
}
