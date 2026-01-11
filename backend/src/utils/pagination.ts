// src/utils/pagination.ts
export function parseLimit(v: unknown, def = 20) {
  const n = Number(v);
  if (!Number.isFinite(n)) return def;
  return Math.max(1, Math.min(50, Math.floor(n)));
}
export function parseCursor(v: unknown) {
  if (typeof v !== "string" || v.trim() === "") return null;
  return v.trim();
}


export type CursorPage<T> = {
  items: T[];
  nextCursor: string | null;
};
