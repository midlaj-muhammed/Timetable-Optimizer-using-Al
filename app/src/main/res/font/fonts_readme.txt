Font Files Required:

To complete the Apple-inspired design, you need to add the following SF Pro font files to this directory:

SF Pro Display:
- sf_pro_display_light.ttf
- sf_pro_display_regular.ttf
- sf_pro_display_medium.ttf
- sf_pro_display_semibold.ttf
- sf_pro_display_bold.ttf

SF Pro Text:
- sf_pro_text_light.ttf
- sf_pro_text_regular.ttf
- sf_pro_text_medium.ttf
- sf_pro_text_semibold.ttf
- sf_pro_text_bold.ttf

Note: SF Pro fonts are Apple's proprietary fonts. For development purposes, you can:
1. Download them from Apple's developer website (requires Apple Developer account)
2. Use alternative fonts like Roboto (already available in Android)
3. Use Google Fonts alternatives like Inter or System fonts

If you don't have access to SF Pro fonts, the app will fall back to system default fonts.

Alternative font suggestions:
- Inter (Google Fonts)
- Roboto (Android default)
- Open Sans (Google Fonts)
- Lato (Google Fonts)

To use alternative fonts, update the font references in:
- app/src/main/java/com/timetableoptimizer/ai/ui/theme/Type.kt
