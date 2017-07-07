import { Injectable, Component } from '@angular/core';
import { Router } from '@angular/router';
import { NgbModal, NgbModalRef } from '@ng-bootstrap/ng-bootstrap';
import { Avaliacao } from './avaliacao.model';
import { AvaliacaoService } from './avaliacao.service';
@Injectable()
export class AvaliacaoPopupService {
    private isOpen = false;
    constructor(
        private modalService: NgbModal,
        private router: Router,
        private avaliacaoService: AvaliacaoService

    ) {}

    open(component: Component, id?: number | any): NgbModalRef {
        if (this.isOpen) {
            return;
        }
        this.isOpen = true;

        if (id) {
            this.avaliacaoService.find(id).subscribe((avaliacao) => {
                this.avaliacaoModalRef(component, avaliacao);
            });
        } else {
            return this.avaliacaoModalRef(component, new Avaliacao());
        }
    }

    avaliacaoModalRef(component: Component, avaliacao: Avaliacao): NgbModalRef {
        const modalRef = this.modalService.open(component, { size: 'lg', backdrop: 'static'});
        modalRef.componentInstance.avaliacao = avaliacao;
        modalRef.result.then((result) => {
            this.router.navigate([{ outlets: { popup: null }}], { replaceUrl: true });
            this.isOpen = false;
        }, (reason) => {
            this.router.navigate([{ outlets: { popup: null }}], { replaceUrl: true });
            this.isOpen = false;
        });
        return modalRef;
    }
}
