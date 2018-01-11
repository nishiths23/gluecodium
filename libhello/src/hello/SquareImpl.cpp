// -------------------------------------------------------------------------------------------------
//
// Copyright (C) 2018 HERE Global B.V. and/or its affiliated companies. All rights reserved.
//
// This software, including documentation, is protected by copyright controlled by
// HERE Global B.V. All rights are reserved. Copying, including reproducing, storing,
// adapting or translating, any or all of this material requires the prior written
// consent of HERE Global B.V. This material also contains confidential information,
// which may not be disclosed to others without prior written consent of HERE Global B.V.
//
// -------------------------------------------------------------------------------------------------

#include "SquareImpl.h"

namespace hello {
SquareImpl::SquareImpl(const double side) : RectangleImpl(side, side) {}

bool SquareImpl::is_rectangle() { return RectangleImpl::is_rectangle(); }

::std::string SquareImpl::get_type() { return "Square"; }

double SquareImpl::get_width() { return RectangleImpl::get_width(); }

double SquareImpl::get_height() { return RectangleImpl::get_height(); }
}
