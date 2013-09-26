/* MessageboxDlg.java

	Purpose:
		
	Description:
		
	History:
		Wed Aug 17 16:42:20     2005, Created by tomyeh

Copyright (C) 2005 Potix Corporation. All Rights Reserved.

{{IS_RIGHT
	This program is distributed under LGPL Version 2.1 in the hope that
	it will be useful, but WITHOUT ANY WARRANTY.
}}IS_RIGHT
*/
package org.zkoss.zul.impl;

import org.zkoss.mesg.Messages;
import org.zkoss.zul.mesg.MZul;

import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.EventListener;

import org.zkoss.zul.Window;
import org.zkoss.zul.Messagebox;

/**
 * Used with {@link Messagebox} to implement a message box.
 *
 * @author tomyeh
 */
public class MessageboxDlg extends Window {
	/** A OK button. */
	public static final int OK = Messagebox.OK;
	/** A Cancel button. */
	public static final int CANCEL = Messagebox.CANCEL;
	/** A Yes button. */
	public static final int YES = Messagebox.YES;
	/** A No button. */
	public static final int NO = Messagebox.NO;
	/** A Abort button. */
	public static final int ABORT = Messagebox.ABORT;
	/** A Retry button. */
	public static final int RETRY = Messagebox.RETRY;
	/** A IGNORE button. */
	public static final int IGNORE = Messagebox.IGNORE;

	/** What buttons are allowed. */
	private int _buttons;
	/** Which button is pressed. */
	private int _result;
	/** The event lisetener. */
	private EventListener _listener;

	public void onOK() throws Exception {
		if ((_buttons & OK) != 0) endModal(Messagebox.ON_OK, OK);
		else if ((_buttons & YES) != 0) endModal(Messagebox.ON_YES, YES);
		else if ((_buttons & RETRY) != 0) endModal(Messagebox.ON_RETRY, RETRY);
	}
	public void onCancel() throws Exception {
		if (_buttons == OK) endModal(Messagebox.ON_OK, OK);
		else if ((_buttons & CANCEL) != 0) endModal(Messagebox.ON_CANCEL, CANCEL);
		else if ((_buttons & NO) != 0) endModal(Messagebox.ON_NO, NO);
		else if ((_buttons & ABORT) != 0) endModal(Messagebox.ON_ABORT, ABORT);
	}

	/** Sets what buttons are allowed. */
	public void setButtons(int buttons) {
		_buttons = buttons;
	}
	/** Sets the event listener.
	 * @param listener the event listener. If null, no invocation at all.
	 * @since 3.0.4
	 */
	public void setEventListener(EventListener listener) {
		_listener = listener;
	}
	/** Sets the focus.
	 * @param button the button to gain the focus. If 0, the default one
	 * (i.e., the first one) is assumed.
	 * @since 3.0.0
	 */
	public void setFocus(int button) {
		if (button > 0) {
			final Button btn = (Button)getFellowIfAny("btn" + button);
			if (btn != null)
				btn.focus();
		}
	}

	/** Called only internally.
	 */
	public void endModal(String evtnm, int button) throws Exception {
		_result = button;
		if (_listener != null) {
			final Event evt = new Event(evtnm, this, new Integer(button));
			_listener.onEvent(evt);
			if (!evt.isPropagatable())
				return; //no more processing
		}
		detach();
	}
	/** Returns the result which is the button being pressed.
	 */
	public int getResult() {
		return _result;
	}

	//Override//
	public void onClose() {
		if (_listener != null) {
			final Event evt = new Event(Events.ON_CLOSE, this, new Integer(-1));
			try {
				_listener.onEvent(evt);
				if (!evt.isPropagatable())
					return; //no more processing
			} catch (Exception ex) {
				throw UiException.Aide.wrap(ex);
			}
		}
		super.onClose();
	}

	/**
	 * Represents a button on the message box.
	 * @since 3.0.0
	 */
	public static class Button extends org.zkoss.zul.Button {
		private int _button;
		private String _evtnm;
		public Button() {
			setAutodisable("self");
		}
		/** Sets the identity.
		 */
		public void setIdentity(int button) {
			_button = button;

			final int label;
			switch (button) {
			case YES:
				label = MZul.YES;
				_evtnm = Messagebox.ON_YES;
				break;
			case NO:
				label = MZul.NO;
				_evtnm = Messagebox.ON_NO;
				break;
			case RETRY:
				label = MZul.RETRY;
				_evtnm = Messagebox.ON_RETRY;
				break;
			case ABORT:
				label = MZul.ABORT;
				_evtnm = Messagebox.ON_ABORT;
				break;
			case IGNORE:
				label = MZul.IGNORE;
				_evtnm = Messagebox.ON_IGNORE;
				break;
			case CANCEL:
				label = MZul.CANCEL;
				_evtnm = Messagebox.ON_CANCEL;
				break;
			default:
				label = MZul.OK;
				_evtnm = Messagebox.ON_OK;
				break;
			}
			setLabel(Messages.get(label));
			setId("btn" + _button);
		}
		public void onClick() throws Exception {
			((MessageboxDlg)getSpaceOwner()).endModal(_evtnm, _button);
		}
		protected String getDefaultMold(Class klass) {
			return super.getDefaultMold(org.zkoss.zul.Button.class);
		}
	}
}
