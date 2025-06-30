/**
 * Convierte un arreglo de objetos a un arreglo de strings con JSON.stringify
 * @param items Arreglo de objetos a serializar
 * @returns Arreglo de strings JSON
 */
export function serializarArray<T extends object>(items: T[]): string[] {
  return items.map((item) => JSON.stringify(item));
}
/**
 * Convierte un  objeto a string con JSON.stringify
 * @returns Arreglo de strings JSON
 */
export function serializarObjeto<T extends object>(obj: T): string {
  return JSON.stringify(obj);
}
