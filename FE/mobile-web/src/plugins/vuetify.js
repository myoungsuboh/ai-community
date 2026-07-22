import 'vuetify/styles'
import { createVuetify } from 'vuetify'

// ── AI 커뮤니티 디자인 시스템 — "Sunny / Storybook" ─────────────────
// refero "Karl" 스타일 참고: 햇살 노랑 배경, 친근한 코발트 블루, 산호 액센트,
// 차콜 잉크 텍스트, 두꺼운 라운드 디스플레이(Changa One), 그림자 대신 라인워크(테두리).
// 콘텐츠 앱 특성상 가독성을 위해: 히어로/액센트엔 노랑, 본문 카드엔 흰색 + 차콜 테두리.
const SUN = '#FFE600'
const AZURE = '#007FFF'
const CORAL = '#EF3B2C'
const INK = '#2B2B2B'

const light = {
  dark: false,
  colors: {
    background: '#FFFBEA', // 부드러운 햇살 크림
    surface: '#FFFFFF',
    'surface-variant': '#FFF3B0', // 연한 노랑 (강조 블록)
    'on-surface-variant': '#5C5A4E',
    primary: AZURE,
    'primary-darken-1': '#006AD6',
    secondary: CORAL,
    accent: SUN,
    info: AZURE,
    success: '#129A6E',
    warning: '#F5A524',
    error: CORAL,
    'on-background': INK,
    'on-surface': INK,
    'on-primary': '#FFFFFF',
    'on-secondary': '#FFFFFF',
  },
  variables: {
    'border-color': INK,
    'border-opacity': 0.16,
  },
}

const dark = {
  dark: true,
  colors: {
    background: '#1A1917', // 따뜻한 차콜
    surface: '#232220',
    'surface-variant': '#2E2C27',
    'on-surface-variant': '#B8B4A6',
    primary: '#3B9BFF',
    'primary-darken-1': '#2B7FE0',
    secondary: '#FF6E54',
    accent: SUN,
    info: '#3B9BFF',
    success: '#2FBE8C',
    warning: '#FBBF24',
    error: '#FF6E54',
    'on-background': '#FBF7EC',
    'on-surface': '#FBF7EC',
    'on-primary': '#FFFFFF',
    'on-secondary': '#1A1917',
  },
}

export default createVuetify({
  theme: {
    defaultTheme: 'light',
    themes: { light, dark },
  },
  defaults: {
    global: { ripple: true },
    VBtn: {
      rounded: 'pill',
      class: 'text-none font-weight-bold',
      style: 'letter-spacing: normal;',
    },
    VCard: {
      rounded: 'xl',
      elevation: 0,
      border: true,
    },
    VChip: { rounded: 'pill' },
    VTextField: { variant: 'outlined', density: 'comfortable', color: 'primary', rounded: 'lg' },
    VTextarea: { variant: 'outlined', color: 'primary', rounded: 'lg' },
    VSelect: { variant: 'outlined', density: 'comfortable', color: 'primary', rounded: 'lg' },
  },
})
