import React, { useState } from "react";
import {
  View,
  Text,
  TouchableOpacity,
  Modal,
  StyleSheet,
  TouchableWithoutFeedback,
  Platform,
} from "react-native";
import DateTimePicker from "@react-native-community/datetimepicker";
import { ProfilePickerProps } from "@/types/type";
import { GENDER_OPTIONS } from "@/constants";

const ProfilePicker = ({ onChange }: ProfilePickerProps) => {
  const [selectedGender, setSelectedGender] = useState("");
  const [genderModalVisible, setGenderModalVisible] = useState(false);

  const [birthdate, setBirthdate] = useState<Date | null>(null);
  const [tempDate, setTempDate] = useState(new Date());
  const [birthdateModalVisible, setBirthdateModalVisible] = useState(false);

  const handleGenderSelect = (value : string) => {
    setSelectedGender(value);
    setGenderModalVisible(false);
    onChange?.({ gender: value, birthdate }); // ← 🔁 notify parent
  };

  const handleBirthdateConfirm = () => {
    setBirthdate(tempDate);
    setBirthdateModalVisible(false);
    onChange?.({ gender: selectedGender, birthdate: tempDate }); // ← 🔁 notify parent
  };

  return (
    <View style={styles.container}>
      {/* Gender Picker */}
      <View style={styles.row}>
        <View style={{flex: 1, marginRight: 8}}>
          <Text style={styles.label}>Gender</Text>
          <TouchableOpacity
            style={styles.dropdownButton}
            onPress={() => setGenderModalVisible(true)}
          >
            <Text style={styles.dropdownText}>
              {selectedGender
                ? GENDER_OPTIONS.find((g) => g.value === selectedGender)?.label
                : "Select"}
            </Text>
          </TouchableOpacity>
        </View>

        {/* Birthdate Picker */}
        <View style={{flex: 1, marginLeft: 8}}>
          <Text style={styles.label}>Birthdate</Text>
          <TouchableOpacity
            style={styles.dropdownButton}
            onPress={() => setBirthdateModalVisible(true)}
          >
            <Text style={styles.dropdownText}>
              {birthdate
                ? birthdate.toLocaleDateString("en-US", {
                    day: "numeric",
                    year: "numeric",
                    month: "long",
                  })
                : "Select"}
            </Text>
          </TouchableOpacity>
        </View>
      </View>
      {/* Gender Modal */}
      <Modal
        transparent
        animationType="fade"
        visible={genderModalVisible}
        onRequestClose={() => setGenderModalVisible(false)}
      >
        <TouchableWithoutFeedback onPress={() => setGenderModalVisible(false)}>
          <View style={styles.modalOverlay}>
            <TouchableWithoutFeedback>
              <View style={styles.modalContent}>
                {GENDER_OPTIONS.map((option) => (
                  <TouchableOpacity
                    key={option.value}
                    onPress={() => handleGenderSelect(option.value)}
                    style={[
                      styles.optionItem,
                      selectedGender === option.value && styles.selectedOption,
                    ]}
                  >
                    <Text>{option.label}</Text>
                  </TouchableOpacity>
                ))}
              </View>
            </TouchableWithoutFeedback>
          </View>
        </TouchableWithoutFeedback>
      </Modal>

      {/* Birthdate Modal (iOS only) */}
      <Modal
        transparent
        animationType="fade"
        visible={birthdateModalVisible}
        onRequestClose={() => setBirthdateModalVisible(false)}
      >
        <TouchableWithoutFeedback
          onPress={() => setBirthdateModalVisible(false)}
        >
          <View style={styles.modalOverlay}>
            <TouchableWithoutFeedback>
              <View style={styles.modalContent}>
                <DateTimePicker
                  value={tempDate}
                  mode="date"
                  display="spinner"
                  textColor="black"
                  maximumDate={new Date()}
                  onChange={(event, selectedDate) => {
                    if (selectedDate) setTempDate(selectedDate);
                  }}
                  style={{ width: 250 }}
                />
                <TouchableOpacity
                  style={styles.confirmButton}
                  onPress={handleBirthdateConfirm}
                >
                  <Text style={{ color: "white" }}>Confirm</Text>
                </TouchableOpacity>
              </View>
            </TouchableWithoutFeedback>
          </View>
        </TouchableWithoutFeedback>
      </Modal>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    paddingHorizontal: 20,
  },
  row: {
    flexDirection: "row",
    justifyContent: "space-between",
    marginBottom: 20,
  },
  label: {
    fontSize: 16,
    color: "#333",
    marginBottom: 12,
    paddingLeft: 3
  },
  dropdownButton: {
    backgroundColor: "#f5f5f5",
    paddingHorizontal: 15,
    paddingVertical: 14,
    borderRadius: 9999,
    
  },
  dropdownText: {
    fontSize: 16,
    color: "grey",
  },
  modalOverlay: {
    flex: 1,
    backgroundColor: "rgba(0,0,0,0.2)",
    justifyContent: "center",
    alignItems: "center",
  },
  modalContent: {
    backgroundColor: "#fff",
    padding: 20,
    borderRadius: 10,
    minWidth: 250,
    elevation: 5,
  },
  optionItem: {
    paddingVertical: 10,
  },
  selectedOption: {
    backgroundColor: "#e0e0e0",
    borderRadius: 5,
  },
  confirmButton: {
    marginTop: 10,
    backgroundColor: "#333",
    paddingVertical: 8,
    paddingHorizontal: 20,
    borderRadius: 5,
    alignItems: "center",
  },
});

export default ProfilePicker;
