import { NextRequest, NextResponse } from "next/server";

const JAVA_API_URL = process.env.NEXT_PUBLIC_JAVA_API_URL || "";

export async function GET(
  request: NextRequest,
  { params }: { params: Promise<{ path: string[] }> }
) {
  const { path } = await params;
  const apiPath = path.join("/");
  const res = await fetch(`${JAVA_API_URL}/api/${apiPath}`);
  const text = await res.text();
  return new NextResponse(text, {
    headers: { "Content-Type": res.headers.get("Content-Type") || "application/json" },
  });
}

export async function POST(
  request: NextRequest,
  { params }: { params: Promise<{ path: string[] }> }
) {
  const { path } = await params;
  const apiPath = path.join("/");
  const body = await request.text();
  const res = await fetch(`${JAVA_API_URL}/api/${apiPath}`, {
    method: "POST",
    body,
    headers: { "Content-Type": "application/json" },
  });
  const text = await res.text();
  return new NextResponse(text, {
    headers: { "Content-Type": res.headers.get("Content-Type") || "application/json" },
  });
}
