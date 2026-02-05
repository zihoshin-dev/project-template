import { z } from "zod";

/**
 * 로그인 폼 스키마
 */
export const loginSchema = z.object({
  email: z
    .string()
    .min(1, "이메일을 입력해주세요")
    .email("올바른 이메일 형식이 아닙니다"),
  password: z
    .string()
    .min(1, "비밀번호를 입력해주세요"),
});

/**
 * 회원가입 폼 스키마
 */
export const signupSchema = z
  .object({
    email: z
      .string()
      .min(1, "이메일을 입력해주세요")
      .email("올바른 이메일 형식이 아닙니다"),
    name: z
      .string()
      .min(2, "이름은 2자 이상이어야 합니다")
      .max(50, "이름은 50자 이하여야 합니다"),
    password: z
      .string()
      .min(8, "비밀번호는 8자 이상이어야 합니다")
      .max(100, "비밀번호는 100자 이하여야 합니다")
      .regex(
        /^(?=.*[a-zA-Z])(?=.*[0-9])/,
        "비밀번호는 영문과 숫자를 포함해야 합니다"
      ),
    confirmPassword: z
      .string()
      .min(1, "비밀번호 확인을 입력해주세요"),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: "비밀번호가 일치하지 않습니다",
    path: ["confirmPassword"],
  });

/**
 * 프로필 수정 폼 스키마
 */
export const updateProfileSchema = z.object({
  name: z
    .string()
    .min(2, "이름은 2자 이상이어야 합니다")
    .max(50, "이름은 50자 이하여야 합니다")
    .optional(),
});

/**
 * 비밀번호 변경 폼 스키마
 */
export const changePasswordSchema = z
  .object({
    currentPassword: z
      .string()
      .min(1, "현재 비밀번호를 입력해주세요"),
    newPassword: z
      .string()
      .min(8, "새 비밀번호는 8자 이상이어야 합니다")
      .max(100, "새 비밀번호는 100자 이하여야 합니다")
      .regex(
        /^(?=.*[a-zA-Z])(?=.*[0-9])/,
        "비밀번호는 영문과 숫자를 포함해야 합니다"
      ),
    confirmNewPassword: z
      .string()
      .min(1, "새 비밀번호 확인을 입력해주세요"),
  })
  .refine((data) => data.newPassword === data.confirmNewPassword, {
    message: "새 비밀번호가 일치하지 않습니다",
    path: ["confirmNewPassword"],
  });

// 타입 추론
export type LoginFormData = z.infer<typeof loginSchema>;
export type SignupFormData = z.infer<typeof signupSchema>;
export type UpdateProfileFormData = z.infer<typeof updateProfileSchema>;
export type ChangePasswordFormData = z.infer<typeof changePasswordSchema>;
