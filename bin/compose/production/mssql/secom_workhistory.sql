-- 데이터베이스 생성
CREATE DATABASE inco;
GO

-- 데이터베이스 사용
USE inco;
GO

-- 데이터베이스 collation 변경
ALTER DATABASE inco COLLATE Korean_Wansung_CI_AS;
GO

CREATE TABLE [dbo].[SECOM_WORKHISTORY](
        [WorkDate]      [varchar](8)    NOT NULL,
        [CardNo]                [varchar](18)   NOT NULL,
        [CardFullData]  [varchar](20)   NULL,
        [JuminNo]               [varchar](13)   NULL,
        [Name]          [nvarchar](50)  NULL,
        [Sabun]         [varchar](13)   NULL,
        [Company]       [varchar](3)    NULL,
        [Department]    [varchar](3)    NULL,
        [Team]          [varchar](3)    NULL,
        [Part]          [varchar](3)    NULL,
        [Grade]         [varchar](3)    NULL,
        [DetailGrade]   [varchar](3)    NULL,
        [WorkGroupCode] [varchar](3)    NULL,
        [WorkGroupName] [nvarchar](40)  NULL,
        [ScheduleID]    [varchar](3)    NULL,
        [ScheduleName]  [nvarchar](40)  NULL,
        [ScheduleType]  [int]           NULL DEFAULT ('0'),
        [WorkType]      [int]           NULL DEFAULT ('0'),
        [bWS]           [int]           NULL DEFAULT ('0'),
        [bWC]           [int]           NULL DEFAULT ('0'),
        [WSTime]                [varchar](14)   NULL,
        [WCTime]                [varchar](14)   NULL,
        [PWType]                [int]           NULL DEFAULT ('0'),
        [PWTime]                [int]           NULL DEFAULT ('0'),
        [OWTime]                [int]           NULL DEFAULT ('0'),
        [NWTime]                [int]           NULL DEFAULT ('0'),
        [TotalWorkTime] [int]           NULL DEFAULT ('0'),
        [NormalWorkTime]        [int]           NULL DEFAULT ('0'),
        [HWTime]                [int]           NULL DEFAULT ('0'),
        [bLate]         [int]           NULL DEFAULT ('0'),
        [bPWC]          [int]           NULL DEFAULT ('0'),
        [bAbsent]               [int]           NULL DEFAULT ('0'),
        [LateTime]              [int]           NULL DEFAULT ('0'),
        [ModifyUser]    [varchar](31)   NULL,
        [ModifyTime]    [varchar](14)   NULL,
        [InsertTime]    [varchar](14)   NULL,
        [UpdateTime]    [varchar](14)   NULL,
        [Version]               [varchar](14)   NULL,
        CONSTRAINT [PK_SECOM_WORKHISTORY] PRIMARY KEY CLUSTERED
        (
                [WorkDate] ASC,
                [CardNo] ASC
        )
) ON [PRIMARY]

