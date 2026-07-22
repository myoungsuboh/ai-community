import 'vuetify/styles'
import { createVuetify } from 'vuetify'

// ── AI 커뮤니티 디자인 시스템 ──────────────────────────────────
// 컨셉: "Large Typography" (styles.refero.design 참고) — 큰 제목, 넉넉한 여백,
// 종이 질감의 뉴트럴 배경 + 전기적인 인디고 액센트. 라이트/다크 모두 지원.
const light = {
  dark: false,
  colors: {
    background: '#F7F6F2',
    surface: '#FFFFFF',
    'surface-variant': '#EFEEE8',
    'on-surface-variant': '#5B5B60',
    primary: '#4B3BFF',
    'primary-darken-1': '#3A2CE0',
    secondary: '#FF5A3C',
    accent: '#0BAF8F',
    info: '#3B82F6',
    success: '#0BAF8F',
    warning: '#F5A524',
    error: '#E5484D',
    'on-background': '#0B0B0C',
    'on-surface': '#0B0B0C',
  },
}

const dark = {
  dark: true,
  colors: {
    background: '#0B0B0C',
    surface: '#141416',
    'surface-variant': '#1E1E22',
    'on-surface-variant': '#A6A6AD',
    primary: '#7C6BFF',
    'primary-darken-1': '#6151F0',
    secondary: '#FF6E54',
    accent: '#22C7A6',
    info: '#60A5FA',
    success: '#22C7A6',
    warning: '#FBBF24',
    error: '#FF6369',
    'on-background': '#F5F5F3',
    'on-surface': '#F5F5F3',
  },
}

export default createVuetify({
  theme: {
    defaultTheme: 'light',
    themes: { light, dark },
  },
  defaults: {
    VBtn: {
      rounded: 'lg',
      class: 'text-none font-weight-bold',
      style: 'letter-spacing: normal;',
    },
    VCard: {
      rounded: 'xl',
      elevation: 0,
      border: true,
    },
    VTextField: {
      variant: 'outlined',
      density: 'comfortable',
      color: 'primary',
    },
    VTextarea: {
      variant: 'outlined',
      color: 'primary',
    },
    VSelect: {
      variant: 'outlined',
      density: 'comfortable',
      color: 'primary',
    },
  },
})
