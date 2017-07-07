import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Response } from '@angular/http';

import { NgbActiveModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { EventManager, AlertService, JhiLanguageService } from 'ng-jhipster';

import { Avaliacao } from './avaliacao.model';
import { AvaliacaoPopupService } from './avaliacao-popup.service';
import { AvaliacaoService } from './avaliacao.service';
import { Prognose, PrognoseService } from '../prognose';

@Component({
    selector: 'jhi-avaliacao-dialog',
    templateUrl: './avaliacao-dialog.component.html'
})
export class AvaliacaoDialogComponent implements OnInit {

    avaliacao: Avaliacao;
    authorities: any[];
    isSaving: boolean;

    prognoses: Prognose[];
    constructor(
        public activeModal: NgbActiveModal,
        private jhiLanguageService: JhiLanguageService,
        private alertService: AlertService,
        private avaliacaoService: AvaliacaoService,
        private prognoseService: PrognoseService,
        private eventManager: EventManager
    ) {
        this.jhiLanguageService.setLocations(['avaliacao']);
    }

    ngOnInit() {
        this.isSaving = false;
        this.authorities = ['ROLE_USER', 'ROLE_ADMIN'];
        this.prognoseService.query().subscribe(
            (res: Response) => { this.prognoses = res.json(); }, (res: Response) => this.onError(res.json()));
    }
    clear() {
        this.activeModal.dismiss('cancel');
    }

    save() {
        this.isSaving = true;
        if (this.avaliacao.id !== undefined) {
            this.avaliacaoService.update(this.avaliacao)
                .subscribe((res: Avaliacao) =>
                    this.onSaveSuccess(res), (res: Response) => this.onSaveError(res));
        } else {
            this.avaliacaoService.create(this.avaliacao)
                .subscribe((res: Avaliacao) =>
                    this.onSaveSuccess(res), (res: Response) => this.onSaveError(res));
        }
    }

    private onSaveSuccess(result: Avaliacao) {
        this.eventManager.broadcast({ name: 'avaliacaoListModification', content: 'OK'});
        this.isSaving = false;
        this.activeModal.dismiss(result);
    }

    private onSaveError(error) {
        try {
            error.json();
        } catch (exception) {
            error.message = error.text();
        }
        this.isSaving = false;
        this.onError(error);
    }

    private onError(error) {
        this.alertService.error(error.message, null, null);
    }

    trackPrognoseById(index: number, item: Prognose) {
        return item.id;
    }
}

@Component({
    selector: 'jhi-avaliacao-popup',
    template: ''
})
export class AvaliacaoPopupComponent implements OnInit, OnDestroy {

    modalRef: NgbModalRef;
    routeSub: any;

    constructor(
        private route: ActivatedRoute,
        private avaliacaoPopupService: AvaliacaoPopupService
    ) {}

    ngOnInit() {
        this.routeSub = this.route.params.subscribe((params) => {
            if ( params['id'] ) {
                this.modalRef = this.avaliacaoPopupService
                    .open(AvaliacaoDialogComponent, params['id']);
            } else {
                this.modalRef = this.avaliacaoPopupService
                    .open(AvaliacaoDialogComponent);
            }
        });
    }

    ngOnDestroy() {
        this.routeSub.unsubscribe();
    }
}
