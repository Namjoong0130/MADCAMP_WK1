/*
  Warnings:

  - Added the required column `createdById` to the `ShareLink` table without a default value. This is not possible if the table is not empty.

*/
-- AlterTable
ALTER TABLE `ShareLink` ADD COLUMN `createdById` VARCHAR(191) NOT NULL;

-- CreateIndex
CREATE INDEX `ShareLink_createdById_idx` ON `ShareLink`(`createdById`);

-- AddForeignKey
ALTER TABLE `ShareLink` ADD CONSTRAINT `ShareLink_createdById_fkey` FOREIGN KEY (`createdById`) REFERENCES `User`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE;
