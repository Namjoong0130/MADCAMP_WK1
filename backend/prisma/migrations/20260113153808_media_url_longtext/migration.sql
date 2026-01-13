/*
  Warnings:

  - You are about to drop the column `deletedAt` on the `Media` table. All the data in the column will be lost.
  - You are about to alter the column `mimeType` on the `Media` table. The data in that column could be lost. The data in that column will be cast from `VarChar(191)` to `VarChar(100)`.

*/
-- AlterTable
ALTER TABLE `Media` DROP COLUMN `deletedAt`,
    MODIFY `url` LONGTEXT NOT NULL,
    MODIFY `mimeType` VARCHAR(100) NULL;

-- CreateTable
CREATE TABLE `CheerTeam` (
    `id` VARCHAR(191) NOT NULL,
    `name` VARCHAR(191) NOT NULL,
    `logoUrl` LONGTEXT NULL,
    `createdAt` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    `deletedAt` DATETIME(3) NULL,

    UNIQUE INDEX `CheerTeam_name_key`(`name`),
    PRIMARY KEY (`id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `CheerMatch` (
    `id` VARCHAR(191) NOT NULL,
    `title` VARCHAR(191) NOT NULL,
    `isActive` BOOLEAN NOT NULL DEFAULT true,
    `startsAt` DATETIME(3) NULL,
    `endsAt` DATETIME(3) NULL,
    `createdAt` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    `deletedAt` DATETIME(3) NULL,
    `homeTeamId` VARCHAR(191) NOT NULL,
    `awayTeamId` VARCHAR(191) NOT NULL,

    INDEX `CheerMatch_isActive_idx`(`isActive`),
    INDEX `CheerMatch_homeTeamId_idx`(`homeTeamId`),
    INDEX `CheerMatch_awayTeamId_idx`(`awayTeamId`),
    PRIMARY KEY (`id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- CreateTable
CREATE TABLE `CheerTap` (
    `id` VARCHAR(191) NOT NULL,
    `matchId` VARCHAR(191) NOT NULL,
    `teamId` VARCHAR(191) NOT NULL,
    `userId` VARCHAR(191) NOT NULL,
    `count` INTEGER NOT NULL DEFAULT 0,
    `createdAt` DATETIME(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3),
    `updatedAt` DATETIME(3) NOT NULL,

    INDEX `CheerTap_matchId_teamId_idx`(`matchId`, `teamId`),
    INDEX `CheerTap_userId_idx`(`userId`),
    UNIQUE INDEX `CheerTap_matchId_teamId_userId_key`(`matchId`, `teamId`, `userId`),
    PRIMARY KEY (`id`)
) DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- AddForeignKey
ALTER TABLE `CheerMatch` ADD CONSTRAINT `CheerMatch_homeTeamId_fkey` FOREIGN KEY (`homeTeamId`) REFERENCES `CheerTeam`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `CheerMatch` ADD CONSTRAINT `CheerMatch_awayTeamId_fkey` FOREIGN KEY (`awayTeamId`) REFERENCES `CheerTeam`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `CheerTap` ADD CONSTRAINT `CheerTap_matchId_fkey` FOREIGN KEY (`matchId`) REFERENCES `CheerMatch`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `CheerTap` ADD CONSTRAINT `CheerTap_teamId_fkey` FOREIGN KEY (`teamId`) REFERENCES `CheerTeam`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE;

-- AddForeignKey
ALTER TABLE `CheerTap` ADD CONSTRAINT `CheerTap_userId_fkey` FOREIGN KEY (`userId`) REFERENCES `User`(`id`) ON DELETE RESTRICT ON UPDATE CASCADE;
