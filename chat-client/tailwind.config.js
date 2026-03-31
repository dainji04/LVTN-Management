/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{js,ts,jsx,tsx}"],
  theme: {
    extend: {
      colors: {
        // Primary color khớp với bg-primary / text-primary trong thiết kế
        primary: {
          DEFAULT: "#F4517A",
          50: "#FFF0F4",
          90: "rgba(244,81,122,0.9)",
        },
      },
      fontFamily: {
        sans: ["-apple-system", "BlinkMacSystemFont", '"Segoe UI"', "Roboto", "sans-serif"],
      },
    },
  },
  plugins: [],
};
