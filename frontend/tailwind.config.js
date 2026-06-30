/** @type {import('tailwindcss').Config} */
export default {
  content: ["./index.html", "./src/**/*.{js,jsx}"],
  theme: {
    extend: {
      colors: {
        // CRED Aesthetic: Deep darks and stark contrasts
        background: "#050505",
        surface: "#0e0e0e",
        surfaceAlt: "#161616",

        textPrimary: "#f5f5f7",
        textSecondary: "#8a8a8e",
        textMuted: "#505050",

        borderSubtle: "rgba(255, 255, 255, 0.05)",

        // Copper / Gold accent (CRED style)
        primary: {
          DEFAULT: "#d4af37",
          50: "#fdf8e6",
          100: "#faefcc",
          200: "#f5e099",
          300: "#efd066",
          400: "#eac033",
          500: "#d4af37", // Gold
          600: "#aa8c2c",
          700: "#7f6921",
          800: "#554616",
          900: "#2a230b",
          950: "#151105",
        },

        // Premium Silver / White for secondary highlights
        accent: {
          DEFAULT: "#ffffff",
          50: "#ffffff",
          100: "#fafafa",
          200: "#f4f4f5",
          300: "#e4e4e7",
          400: "#d4d4d8",
          500: "#a1a1aa",
          600: "#71717a",
          700: "#52525b",
          800: "#3f3f46",
          900: "#27272a",
          950: "#18181b",
        },

        // Deep dark scale
        dark: {
          50: "#fafafa",
          100: "#f5f5f5",
          200: "#e5e5e5",
          300: "#d4d4d4",
          400: "#a3a3a3",
          500: "#737373",
          600: "#525252",
          700: "#404040",
          800: "#262626",
          900: "#171717",
          950: "#050505",
        },
        
        success: {
          50: '#f0fdf4',
          100: '#dcfce7',
          200: '#bbf7d0',
          300: '#86efac',
          400: '#4ade80',
          500: '#22c55e',
          600: '#16a34a',
          700: '#15803d',
        },
        warning: {
          50: '#fffbeb',
          100: '#fef3c7',
          200: '#fde68a',
          300: '#fcd34d',
          400: '#fbbf24',
          500: '#f59e0b',
          600: '#d97706',
          700: '#b45309',
        },
        danger: {
          50: '#fef2f2',
          100: '#fee2e2',
          200: '#fecaca',
          300: '#fca5a5',
          400: '#f87171',
          500: '#ef4444',
          600: '#dc2626',
          700: '#b91c1c',
        },
      },
      fontFamily: {
        sans: ['Inter', 'system-ui', '-apple-system', 'BlinkMacSystemFont', 'Segoe UI', 'Roboto', 'sans-serif'],
      },
      boxShadow: {
        // Neumorphic Shadows for CRED aesthetic (assuming surface is #0e0e0e)
        'neu-flat': '6px 6px 12px #090909, -6px -6px 12px #131313',
        'neu-pressed': 'inset 6px 6px 12px #090909, inset -6px -6px 12px #131313',
        'neu-elevated': '10px 10px 20px #070707, -10px -10px 20px #151515',
        
        'glow': '0 0 20px rgba(212, 175, 55, 0.25)', // Gold glow
        'glow-lg': '0 0 40px rgba(212, 175, 55, 0.40)',
        'card': '0 10px 30px -10px rgba(0, 0, 0, 0.8)',
        'card-hover': '0 20px 40px -10px rgba(0, 0, 0, 0.9)',
        'inner-light': 'inset 0 1px 1px 0 rgba(255, 255, 255, 0.05)',
      },
      animation: {
        'fade-in': 'fadeIn 0.5s ease-out',
        'fade-in-up': 'fadeInUp 0.5s cubic-bezier(0.16, 1, 0.3, 1)',
        'fade-in-down': 'fadeInDown 0.3s cubic-bezier(0.16, 1, 0.3, 1)',
        'slide-in-right': 'slideInRight 0.3s cubic-bezier(0.16, 1, 0.3, 1)',
        'slide-in-left': 'slideInLeft 0.3s cubic-bezier(0.16, 1, 0.3, 1)',
        'scale-in': 'scaleIn 0.3s cubic-bezier(0.16, 1, 0.3, 1)',
        'pulse-slow': 'pulse 3s cubic-bezier(0.4, 0, 0.6, 1) infinite',
        'shimmer': 'shimmer 2.5s infinite linear',
        'bounce-subtle': 'bounceSubtle 2s infinite',
      },
      keyframes: {
        fadeIn: {
          '0%': { opacity: '0' },
          '100%': { opacity: '1' },
        },
        fadeInUp: {
          '0%': { opacity: '0', transform: 'translateY(30px)' },
          '100%': { opacity: '1', transform: 'translateY(0)' },
        },
        fadeInDown: {
          '0%': { opacity: '0', transform: 'translateY(-20px)' },
          '100%': { opacity: '1', transform: 'translateY(0)' },
        },
        slideInRight: {
          '0%': { opacity: '0', transform: 'translateX(30px)' },
          '100%': { opacity: '1', transform: 'translateX(0)' },
        },
        slideInLeft: {
          '0%': { opacity: '0', transform: 'translateX(-30px)' },
          '100%': { opacity: '1', transform: 'translateX(0)' },
        },
        scaleIn: {
          '0%': { opacity: '0', transform: 'scale(0.92)' },
          '100%': { opacity: '1', transform: 'scale(1)' },
        },
        shimmer: {
          '0%': { backgroundPosition: '-200% 0' },
          '100%': { backgroundPosition: '200% 0' },
        },
        bounceSubtle: {
          '0%, 100%': { transform: 'translateY(0)' },
          '50%': { transform: 'translateY(-4px)' },
        },
      },
      backgroundImage: {
        'gradient-radial': 'radial-gradient(var(--tw-gradient-stops))',
        'gradient-conic': 'conic-gradient(from 180deg at 50% 50%, var(--tw-gradient-stops))',
        'mesh-gradient': 'linear-gradient(135deg, #d4af37 0%, #aa8c2c 100%)',
        'dark-gradient': 'linear-gradient(180deg, #050505 0%, #0e0e0e 55%, #050505 100%)',
      },
      borderRadius: {
        '4xl': '2rem',
        '5xl': '2.5rem',
      },
      backdropBlur: {
        xs: '2px',
        md: '12px',
        lg: '24px',
      },
      ringOffsetColor: {
        background: "#050505",
        surface: "#0e0e0e",
        surfaceAlt: "#161616",
      },
    },
  },
  plugins: [],
}
