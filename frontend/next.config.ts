import type { NextConfig } from "next";

const nextConfig: NextConfig = {
  // React 19 strict mode
  reactStrictMode: true,

  // 환경 변수 공개 설정
  env: {
    NEXT_PUBLIC_API_URL: process.env.NEXT_PUBLIC_API_URL,
  },

  // 이미지 도메인 설정 (필요시 추가)
  images: {
    remotePatterns: [
      // {
      //   protocol: 'https',
      //   hostname: 'example.com',
      // },
    ],
  },

  // 리다이렉트 설정
  async redirects() {
    return [
      // 예: 루트에서 대시보드로 리다이렉트
      // {
      //   source: '/',
      //   destination: '/dashboard',
      //   permanent: false,
      // },
    ];
  },

  // 헤더 설정 (보안)
  async headers() {
    return [
      {
        source: "/:path*",
        headers: [
          {
            key: "X-Frame-Options",
            value: "DENY",
          },
          {
            key: "X-Content-Type-Options",
            value: "nosniff",
          },
          {
            key: "Referrer-Policy",
            value: "strict-origin-when-cross-origin",
          },
        ],
      },
    ];
  },
};

export default nextConfig;
