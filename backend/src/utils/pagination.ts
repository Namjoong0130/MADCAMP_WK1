export function parseLimit(input: unknown, defaultValue = 20) {
  const n = Number(input);
  if (!Number.isFinite(n)) return defaultValue;
  return Math.max(1, Math.min(50, Math.floor(n)));
}

export function parseCursor(input: unknown) {
  if (typeof input !== "string" || input.trim() === "") return null;
  return input.trim();
}

export type CursorPage<T> = {
  items: T[];
  nextCursor: string | null;
};
