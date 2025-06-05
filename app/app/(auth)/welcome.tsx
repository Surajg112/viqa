import {
  StyleSheet,
  Text,
  View,
  SafeAreaView,
  Pressable,
  Image,
  Button,
} from "react-native";
import React, { useRef, useState } from "react";
import { router } from "expo-router";
import Swiper from "react-native-swiper";
import { onboard } from "../../constants";

export default function Welcome() {
  const swiperRef = useRef<Swiper>(null);
  const [activeIndex, setActiveIndex] = useState(0);
  const isLastSlide = activeIndex === onboard.length - 1
  return (
    <SafeAreaView style={styles.container}>
      <View style={styles.skipContainer}>
        <Pressable onPress={() => router.push("/(auth)/sign-up")}>
          <Text style={styles.skipText}>{`Skip`}</Text>
        </Pressable>
      </View>
      <Swiper
        ref={swiperRef}
        loop={false}
        dot={<View style={styles.dot}></View>}
        activeDot={<View style={styles.activeDot}></View>}
        onIndexChanged={(index) => setActiveIndex(index)}
      >
        {onboard.map((item) => (
          <View key={item.id} style={styles.onboardContainer}>
            <Image
              source={item.image}
              style={styles.image}
              resizeMode="contain"
            />
            <View>
              <Text style={styles.onboardTitle}>{item.title}</Text>
              <Text style={styles.onboardDescription}>{item.description}</Text>
            </View>
          </View>
        ))}
      </Swiper>
      {/* <Button title={isLastSlide ? "Get Started" : "Next" } onPress={() => isLastSlide ? router.replace("/(auth)/sign-up") : swiperRef.current?.scrollBy(1)}></Button> */}
      <Pressable onPress={() => isLastSlide ? router.push("/(auth)/sign-up") : swiperRef.current?.scrollBy(1)} style={styles.button}>
        <Text style={styles.buttonText}>{isLastSlide ? "Get Started" : "Next"}</Text>
      </Pressable>
    </SafeAreaView>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: "center",
    justifyContent: "space-between",
  },
  skipContainer: {
    width: "100%",
    alignItems: "flex-end",
    padding: 10,
  },
  skipText: {
    fontSize: 16,
    marginEnd: 16,
    fontWeight: 400,
  },
  dot: {
    width: 32,
    height: 4,
    marginLeft: 1,
    backgroundColor: "#e2e8f0",
    borderRadius: 100,
  },
  activeDot: {
    width: 32,
    height: 4,
    marginLeft: 1,
    backgroundColor: "#0286ff",
    borderRadius: 100,
  },
  image: {
    width: "100%",
    height: 300,
  },
  onboardContainer: {
    // flex: 1,
    alignItems: "center",
    justifyContent: "center",
    padding: 5
  },
  onboardTitle: {
    fontSize: 24,
    fontWeight: "bold",
    textAlign: "center",
    marginTop: 5
  },
  onboardDescription: {
    fontSize: 16,
    textAlign: "center",
    marginTop: 10
  },
  button: {
    backgroundColor: "#376ca8",
    height: 40,
    width: "40%",
    alignItems: "center",
    justifyContent: "center",
    borderRadius: 15
  },
  buttonText: {
    fontSize: 16,
    fontWeight: "400",
    color: "#ffffff"
  }
});
