/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    "./src/**/*.{html,js}",
    "./node_modules/flowbite/**/*.js"
  ],
  theme: {
    fontFamily: {
      'noto': ['Noto Sans KR'],
    },
  },
  plugins: [
    require('flowbite/plugin')
  ],
}
