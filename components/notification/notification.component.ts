import { Component, OnInit } from '@angular/core';
import { NotificationService, Notification } from '../../servicios/notification.service';

@Component({
    selector: 'app-notification',
    templateUrl: './notification.component.html',
    styleUrls: ['./notification.component.css'],
    standalone: false
})
export class NotificationComponent implements OnInit {
    notification: Notification | null = null;
    timeoutId: any;

    constructor(private notificationService: NotificationService) { }

    ngOnInit(): void {
        this.notificationService.notification$.subscribe(notification => {
            this.notification = notification;
            this.autoDismiss();
        });
    }

    autoDismiss() {
        if (this.timeoutId) {
            clearTimeout(this.timeoutId);
        }
        this.timeoutId = setTimeout(() => {
            this.notification = null;
        }, 3000);
    }

    close() {
        this.notification = null;
        if (this.timeoutId) {
            clearTimeout(this.timeoutId);
        }
    }
}
