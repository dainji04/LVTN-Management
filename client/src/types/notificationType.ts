export type NotificationType = 'like' | 'comment' | 'friend_request' | 'mention' | 'new_post';

export interface Notification {
  id: string;
  type: NotificationType;
  user: {
    name: string;
    avatar?: string;
    avatarColor?: string;
    avatarIcon?: string;
  };
  content: string;
  timeAgo: string;
  isRead: boolean;
  groupName?: string;
  mentionedUser?: string;
  postTitle?: string;
}

